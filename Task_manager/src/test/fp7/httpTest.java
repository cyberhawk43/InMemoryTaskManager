package test.fp7;


import managment.http.HttpTaskManager;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managment.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import task.*;
import task.adapter.Adapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class httpTest {

    protected HttpClient client = HttpClient.newHttpClient();
    protected Gson gson = Adapter.allRegister(new GsonBuilder());
    protected HttpTaskServer server;

    public httpTest()  {
    }


    @BeforeEach
    void init() throws IOException, InterruptedException {

        server = new HttpTaskServer();
        server.start();
        Task task1 = new Task("TaskName1", "TaskDesc1", 0, Status.NEW, LocalDateTime.now(),
                Duration.of(5, DAYS));
        Task task2 = new Task("TaskName2", "TaskDesc2", 0, Status.NEW, LocalDateTime
                .of(1996, 1, 24, 12, 13),
                Duration.of(10, DAYS));
        Epic epic1 = new Epic("EpicName1", "EpicDesc1", Status.NEW);
        Subtask subtask1 = new Subtask("NameSubtask1", "DescSubtask2", 0, Status.NEW
                , 2, LocalDateTime.of(2004, 12, 14, 2, 10),
                Duration.ofDays(1));


        String jsonTask1 = gson.toJson(task1);
        String jsonTask2 = gson.toJson(task2);
        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask = gson.toJson(subtask1);
        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest requestTask1 = HttpRequest.newBuilder()
                .uri(urlTask)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();
        HttpRequest requestTask2 = HttpRequest
                .newBuilder()
                .uri(urlTask)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask2))
                .build();
        HttpRequest requestEpic1 = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        HttpRequest requestSubtask1 = HttpRequest
                .newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();
        HttpResponse<String> responseTask1 = client.send(requestTask1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseTask2 = client.send(requestTask2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEpic1 = client.send(requestEpic1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        //TASK
        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).GET().build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        final Map<Integer, Task> tasks = gson.fromJson(responseTask.body(), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        assertEquals(200, responseTask.statusCode());
        assertNotNull(tasks, "tasks пустой");
        assertEquals(2, tasks.size());


        //EPIC
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).GET().build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        final Map<Integer, Epic> epicsWithSubtasks = gson.fromJson(responseEpic.body(),
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
        assertEquals(200, responseEpic.statusCode());
        assertNotNull(epicsWithSubtasks, "epics пустой");
        assertEquals(1, epicsWithSubtasks.size());

        //SUBTASK
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).GET().build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());


        final Map<Integer, Subtask> subtasks = gson.fromJson(responseSubtask.body(), new TypeToken<HashMap<Integer
                , Subtask>>() {
        }.getType());

        assertEquals(200, responseSubtask.statusCode());
        assertNotNull(subtasks, "subtasks пустой");
        assertEquals(1, subtasks.size());

        //TASK по айди
        URI urlTaskByID = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest requestTaskByID = HttpRequest.newBuilder().uri(urlTaskByID).GET().build();
        HttpResponse<String> responseTaskByID = client.send(requestTaskByID, HttpResponse.BodyHandlers.ofString());
        Task expectedTask = new Task("TaskName1", "TaskDesc1", 0, Status.NEW, LocalDateTime.now(),
                Duration.of(5, DAYS));

        final Task task = gson.fromJson(responseTaskByID.body(), new TypeToken<Task>() {
        }.getType());
        assertEquals(200, responseTaskByID.statusCode());
        assertEquals(expectedTask, task);

        //EPIC по айди
        URI urlEpicByID = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest requestEpicByID = HttpRequest.newBuilder().uri(urlEpicByID).GET().build();
        HttpResponse<String> responseEpicByID = client.send(requestEpicByID, HttpResponse.BodyHandlers.ofString());
        Epic expectedEpic = new Epic("EpicName1", "EpicDesc1", Status.NEW);

        final Epic epic = gson.fromJson(responseEpicByID.body(), new TypeToken<Epic>() {
        }.getType());
        assertEquals(200, responseEpicByID.statusCode());
//        assertEquals(expectedEpic, epic);

        //SUBTASK по айди
        URI urlSubtaskByID = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest requestSubtaskByID = HttpRequest.newBuilder().uri(urlSubtaskByID).GET().build();
        HttpResponse<String> responseSubtaskByID = client.send(requestSubtaskByID, HttpResponse.BodyHandlers.ofString());
        Subtask expectedSubtask = new Subtask("NameSubtask1", "DescSubtask2", 3, Status.NEW
                , 2, LocalDateTime.of(2004, 12, 14, 2, 10),
                Duration.ofDays(1));
        final Subtask subtask = gson.fromJson(responseSubtaskByID.body(), new TypeToken<Subtask>() {
        }.getType());
        assertEquals(200, responseSubtaskByID.statusCode());
        assertEquals(expectedSubtask, subtask);

    }

    @Test
    public void createTask() throws IOException, InterruptedException {
        //task
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("Перекус", "Съесть яблоко", 0, Status.NEW,
                LocalDateTime.of(2077, 12, 12, 12, 12), Duration.of(10, DAYS));
        String jsonTask = gson.toJson(task);
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseTask.statusCode());
        assertEquals(3, manager.getTasks().size(), "Не верное количество задач");


        //epic
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("Epic2", "random", Status.NEW);
        String jsonEpic = gson.toJson(epic);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        manager.load();
        System.out.println(manager.getEpics());
        assertEquals(200, responseEpic.statusCode());
        assertEquals(2, manager.getEpics().size(), "Не верное количество задач");

        //subtask
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask = new Subtask("subtask2", "random", 0, Status.NEW, 5,
                LocalDateTime.of(2047, 12, 12, 12, 12), Duration.of(10, DAYS));
        String jsonSubtask = gson.toJson(subtask);
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        manager.load();

        assertEquals(200, responseSubtask.statusCode());
        assertEquals(2, manager.getSubtasks().size(), "Не верное количество задач");
    }

    @Test
    public void delete() throws IOException, InterruptedException {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");

        URI urlTask = URI.create("http://localhost:8080/tasks/task");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .DELETE()
                .build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseTask.statusCode());
        assertEquals(0, manager.getTasks().size(), "Не верное количество задач");


        //epic
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic)
                .DELETE()
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseEpic.statusCode());
        assertEquals(0, manager.getEpics().size(), "Не верное количество задач");

        //subtask
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask)
                .DELETE()
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseSubtask.statusCode());
        assertEquals(0, manager.getSubtasks().size(), "Не верное количество задач");
    }

    @Test
    public void deleteById() throws IOException, InterruptedException {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        URI urlTask = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .DELETE()
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseTask.statusCode());
        assertEquals(1, manager.getTasks().size(), "Не верное количество задач");
        assertEquals(null, manager.getTaskById(0));
        //subtask
        System.out.println(manager.getSubtaskById(3));
        URI urlSubtask = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask)
                .DELETE()
                .build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseSubtask.body());
        manager.load();
        assertEquals(200, responseSubtask.statusCode());
        assertEquals(0, manager.getSubtasks().size(), "Не верное количество задач");
        //epic
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic)
                .DELETE()
                .build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        manager.load();
        System.out.println("response " + responseEpic.body());
        System.out.println("epic " + manager.getEpics());
        assertEquals(200, responseEpic.statusCode());
        assertEquals(0, manager.getEpics().size(), "Не верное количество задач");
    }

    @Test
    public void allDel () throws IOException, InterruptedException {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> responseTask = client.send(request, HttpResponse.BodyHandlers.ofString());
        manager.load();
        assertEquals(200, responseTask.statusCode());
        assertEquals(0, manager.getTasks().size(), "Не верное количество задач");
        assertEquals(0, manager.getSubtasks().size(), "Не верное количество задач");
        assertEquals(0, manager.getEpics().size(), "Не верное количество задач");
    }

}




