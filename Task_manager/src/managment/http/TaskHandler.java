package managment.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managment.Managers;

import task.*;
import task.adapter.Adapter;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    private static HttpTaskManager manager;

    static {
        try {
            manager = (HttpTaskManager) Managers.getDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        gson = Adapter.allRegister(new GsonBuilder());
        int responseCode = 0;
        String response = "";
        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        String method = httpExchange.getRequestMethod();
        URI url = httpExchange.getRequestURI();
        String query = url.getQuery();

        switch (method) {
            case "GET":
                if (path.endsWith("/tasks/task/")) {
                    int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                    if (manager.getTaskById(id) != null) {
                        Task task = manager.getTaskById(id);
                        response = gson.toJson(task);
                    } else {
                        response = gson.toJson("Задачи с id = " + id + " нет");
                    }
                } else if (path.endsWith("/tasks/subtask/")) {
                    int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                    if (manager.getSubtaskById(id) != null) {
                        Subtask subtask = manager.getSubtaskById(id);
                        response = gson.toJson(subtask);
                    } else {
                        response = gson.toJson("Подзадачи с id = " + id + " нет");
                    }

                } else if (path.endsWith("/tasks/epic/")) {
                    int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                    if (manager.getEpicById(id) != null) {
                        Epic epic = manager.getEpicById(id);
                        response = gson.toJson(epic);

                    } else {
                        response = gson.toJson("Эпика с id = " + id + " нет");
                    }
                } else if (path.endsWith("/tasks")) {
                    Map<Integer, Task> allTasks = new HashMap<>();
                    for (Task task : manager.getTasks().values()) {
                        allTasks.put(task.getIdTask(), task);
                    }
                    for (Subtask subtask : manager.getSubtasks().values()) {
                        allTasks.put(subtask.getIdTask(), subtask);
                    }
                    for (Epic epic : manager.getEpics().values()) {
                        allTasks.put(epic.getIdTask(), epic);
                    }
                    if (allTasks.size() == 0) {
                        response = "Задачи еще не созданы";
                    } else {
                        response = gson.toJson(allTasks);
                    }
                } else if (path.endsWith("/tasks/task")) {
                    if (manager.getTasks().size() != 0) {
                        Map<Integer, Task> tasks = manager.getTasks();
                        response = gson.toJson(tasks);
                    } else {
                        response = gson.toJson("Список задач пуст");
                    }
                } else if (path.endsWith("/tasks/subtask")) {
                    if (manager.getSubtasks().size() != 0) {
                        Map<Integer, Subtask> subtasks = manager.getSubtasks();
                        response = gson.toJson(subtasks);
                    } else {
                        response = gson.toJson("Список подзадач пуст");
                    }
                } else if (path.endsWith("/tasks/epic")) {
                    if (manager.getEpics().size() != 0) {
                        Map<Integer, Epic> epics = manager.getEpics();
                        response = gson.toJson(epics);
                    } else {
                        response = gson.toJson("Список эпиков пуст");
                    }
                } else if (path.endsWith("/tasks/history")) {
                    List<Task> history = manager.history();
                    response = gson.toJson(history);
                }
                break;
            case "POST":
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    List<String> listBody = bodyToList(body);

                    if (path.endsWith("/tasks/task")) {
//                        {"id":1,
//                        "type":"TASK",
//                        "name":"TaskName",
//                        "status":"NEW",
//                        "description":"TaskDescription",
//                        "startTime":"14.06.2022 13-17",
//                        "duration":"1"}
                        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm");
                        TaskType type = TaskType.valueOf(listBody.get(1));
                        String name = listBody.get(2);
                        Status status = Status.valueOf(listBody.get(3));
                        String desc = listBody.get(4);
                        Duration duration = Duration.ofDays(Long.parseLong(listBody.get(6)));
                        int id = Integer.parseInt(listBody.get(0));
                        LocalDateTime time = LocalDateTime.parse(listBody.get(5), FORMATTER);
                        Task task = new Task(name, desc, id, status, time, duration);
                        manager.createNewTask(task);
                        response = gson.toJson("задача успешно создана!");
                    } else if (path.endsWith("/tasks/subtask")) {
                        //           {"epicID":"0",
                        //           "id":1,
                        //           "type":"SUBTASK",
                        //           "name":"subtaskName",
                        //           "status":"NEW",
                        //           "description":"subtaskDescription",
                        //           "startTime":"25.08.2018 23-22",
                        //           "duration":"2"}
                        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm");
                        LocalDateTime time = LocalDateTime.parse(listBody.get(6), FORMATTER);
                        TaskType type = TaskType.valueOf(listBody.get(2));
                        String name = listBody.get(3);
                        Status status = Status.valueOf(listBody.get(4));
                        String desc = listBody.get(5);
                        int id = Integer.parseInt(listBody.get(1));
                        Duration duration = Duration.ofDays(Long.parseLong(listBody.get(7)));
                        int epicID = Integer.parseInt(listBody.get(0));
                        if (manager.getEpicById(epicID) != null) {
                            Subtask subtask = new Subtask(name, desc, id, status, epicID, time, duration);
                            manager.createNewSubtask(subtask);
                            manager.getEpicById(epicID).getListSubtask().put(subtask.getIdTask(), subtask);
                            manager.updateEpic(manager.getEpicById(epicID));
                            response = gson.toJson("Подзадача успешно создана");
                        } else {
                            response = gson.toJson("Нет эпиков с таким ID");
                        }

                    } else if (path.endsWith("/tasks/epic")) {
//                        {"id":0,
//                        "type":"EPIC",
//                        "name":"EpicName1",
//                        "status":"NEW",
//                        "description":"EpicDesc1",
//                        "startTime":"null",
//                        "duration":0,
//                        "getEndTime":"null"}

                        TaskType type = TaskType.valueOf(listBody.get(1));
                        String name = listBody.get(2);
                        Status status = Status.valueOf(listBody.get(3));
                        String desc = listBody.get(4);
                        int id = java.lang.Integer.parseInt(listBody.get(0));
                        manager.createNewEpic(new Epic(name, desc, status));

                        response = gson.toJson("Эпик успешно создан");
                    } else {
                        response = gson.toJson("Произошла ошибка при создании задачи!");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "DELETE":
                try {
                    if (path.endsWith("/tasks/task")) {
                        manager.clearTask();
                        response = gson.toJson("Все задачи удалены");
                    } else if (path.endsWith("tasks/subtask")) {
                        manager.clearSubtask();
                        response = gson.toJson("Все подзадачи удалены");
                    } else if (path.endsWith("/tasks/epic")) {
                        manager.clearEpic();
                        response = gson.toJson("Все эпики и их подзадачи удалены");
                    } else if (path.endsWith("/tasks/task/")) {
                        int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                        if (manager.getTaskById(id) != null) {
                            manager.removeTaskById(id);
                            response = gson.toJson("Задача с id= " + id + " Удалена");
                        } else {
                            response = gson.toJson("Задачи с таким id= " + id + " нет");
                        }
                    } else if (path.endsWith("/tasks/subtask/")) {
                        int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                        if (manager.getSubtaskById(id) != null) {
                            manager.removeSubtaskById(id);
                            response = gson.toJson("Подзадача с id= " + id + " удалена");
                        } else {
                            response = gson.toJson("Подзадачи с таким id= " + id + " нет");
                        }
                    } else if (path.endsWith("/tasks/epic/")) {
                        int id = Integer.parseInt(String.valueOf(query.charAt(query.length() - 1)));
                        if (manager.getEpicById(id) != null) {
                            manager.removeEpicById(id);
                            response = gson.toJson("Эпик с id= " + id + " удален");
                        } else {
                            response = gson.toJson("Эпика с таким id= " + id + " нет");
                        }
                    } else if (path.endsWith("/tasks")) {
                        manager.clearTask();
                        manager.clearEpic();
                        response = gson.toJson("Все задачи, эпики и подзадачи удалены");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                response = gson.toJson(method);
        }


        responseCode = 200;
        httpExchange.sendResponseHeaders(responseCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    public static List<String> bodyToList(String body) {
        String[] splitBody = body.split(",");
        List<String> index = new ArrayList<>();
        for (String s : splitBody) {
            String sChange = s.replace("\"", "");
            sChange = sChange.replace("}", "");
            int indexSplit = sChange.indexOf(":");
            index.add(sChange.substring(indexSplit + 1));
        }
        return index;
    }
}