import managment.inMemory.InMemoryHistoryManager;
import managment.Managers;
import managment.TaskManager;

import task.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;


public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager manager = Managers.getDefault();
        InMemoryHistoryManager hist = new InMemoryHistoryManager();
        Task firstTask = new Task("Перекус", "Съесть яблоко",0, Status.NEW, LocalDateTime.now(),
                Duration.of(10,DAYS));
        Task secondTask = new Task("Task2", "Desc Task2",0, Status.NEW, LocalDateTime
                .of(1999, 3, 25, 12, 0),
                Duration.of(1, DAYS));
        Epic firstEpic = new Epic("Убрать снег", "Чистка двора у дома", Status.NEW);
        Subtask firstSubtask = new Subtask("Subtask1", "Desc Subtask1"
                ,0, Status.DONE, firstEpic.getIdTask(), LocalDateTime
                .of(1997, 1, 12, 10, 10),
                Duration.of(1, DAYS));
        Subtask secondSubtask = new Subtask("Subtask2", "Desc Subtask2" ,0,
                Status.NEW, firstEpic.getIdTask(), LocalDateTime.of(1991, 1, 13, 11, 0)
                , Duration.of(1, DAYS));
        firstEpic = new Epic("Убрать снег", "Чистка двора у дома",
                Status.NEW);
        Epic secondEpic = new Epic("Отдохнуть", "Отдых после проделанной работы", Status.NEW);
        secondEpic = new Epic("Отдохнуть", "Отдых после проделанной работы",
                Status.NEW);
        manager.createNewTask(firstTask);
        manager.createNewTask(secondTask);


        //Исправил удаление Epic и после каждого запроса вывожу историю.
        manager.createNewTask(firstTask);
        manager.createNewTask(secondTask);
        manager.createNewEpic(firstEpic);
        manager.createNewSubtask(firstSubtask);
        firstSubtask.setEpicId(firstEpic.getIdTask());
        firstEpic.getListSubtask().put(firstSubtask.getIdTask(), firstSubtask);
        manager.createNewSubtask(secondSubtask);
        secondSubtask.setEpicId(firstEpic.getIdTask());
        firstEpic.getListSubtask().put(secondSubtask.getIdTask(), secondSubtask);
        firstEpic.getStatus();
        manager.createNewEpic(secondEpic);
        Task secTasky = new Task("кус", "Съесть яблоко",1, Status.NEW, LocalDateTime
                .of(1999, 3, 25, 12, 0),
                Duration.of(10,DAYS));
        manager.updateTask(secTasky);
        System.out.println(manager.getTasks().get(1));
        System.out.println(secondTask.getIdTask());
        System.out.println(secTasky.getIdTask());






        System.out.println("Первая задача");
        System.out.println(firstTask);
        System.out.println("\n вторая задач");
        System.out.println(secondTask);
        System.out.println("\n Первый эпик с подзадачами");
        System.out.println(firstEpic);
        System.out.println("\n второй эпик с подзадачами");
        System.out.println(secondEpic);

        System.out.println("\n обновленный первый эпик с подзадачами");
        System.out.println(firstEpic);
        System.out.println("\n Удаление задачи:");
        manager.removeTaskById(1);
        System.out.println(manager.getTaskById(1));
        System.out.println("\n Удаление подзадачи у эпика");
        manager.removeSubtaskById(6);
        System.out.println(secondEpic);
        manager.getTaskById(0);
        System.out.println("\nИстория :" + manager.history());
        manager.getTaskById(1);


        //История после удаления второй задачи
        System.out.println("\nИстория" + manager.history());


    }

}