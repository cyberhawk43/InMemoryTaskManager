package test.fp6;

import managment.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    abstract void setManager();

    @BeforeEach
    public void beforeEach() {
        setManager();
    }

    @Test
    void createNewId() {
        Task firstTask = new Task("Task1", "Desc Task1", 0, Status.NEW, LocalDateTime
                .of(2000, 2, 24, 12, 0),
                Duration.of(1, DAYS));
        Task secondTask = new Task("Task2", "Desc Task2", 0, Status.NEW, LocalDateTime
                .of(1999, 2, 24, 11, 0),
                Duration.of(1, DAYS));
        manager.createNewTask(firstTask);
        manager.createNewTask(secondTask);
        final int expectedID = 1;
        final int actualID = manager.getTasks().get(1).getIdTask();
        assertNotNull(manager.getTasks().get(1), "такой задачи нет.");
        assertEquals(expectedID, actualID, "ID не совпадают.");
    }

    @Test
    void createNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.of(2000, 2, 24, 12, 0), Duration.of(2, DAYS));
        manager.createNewTask(task);
        final Task checkTask = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.of(2000, 2, 24, 12, 0), Duration.of(2, DAYS));
        assertNotNull(task, "Задача не найдена.");
        assertEquals(task, checkTask, "Задачи не совпадают.");
        final Map<Integer, Task> tasks = manager.getTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(checkTask, tasks.get(0), "Задачи не совпадают.");

    }

    @Test
    void createNewSubtask() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime
                .of(2000, 2, 24, 12, 0), Duration.of(2, DAYS));
        manager.createNewSubtask(subtask);
        epic.getListSubtask().put(subtask.getIdTask(), subtask);
        final Subtask checkSubtask = new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", 1, Status.NEW, epic.getIdTask(), LocalDateTime
                .of(2000, 2, 24, 12, 0), Duration.of(2, DAYS));
        assertNotNull(manager.getSubtasks(), "Задача не найдена.");
        assertEquals(epic.getListSubtask().get(1), checkSubtask, "подзадачи не совпадают.");
        assertEquals(epic, manager.getEpics().get(subtask.getEpicId()), "эпики не совпадают");
        final Map<Integer, Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(1), "Задачи не совпадают. ");

    }

    @Test
    void createNewEpic() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        Subtask subtaskNew = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime.of(1999, 2, 1, 4,
                0), Duration.of(1, DAYS));
        Subtask subtaskDone = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                0, Status.DONE, epic.getIdTask(), LocalDateTime.of(2005, 2, 1, 4,
                0), Duration.of(1, DAYS));
        Subtask subtaskInProgress = new Subtask("Test addNewSubtask",
                "Test addNewSubtask description", 0, Status.IN_PROGRESS, epic.getIdTask(), LocalDateTime
                .of(2022, 2, 1, 4, 0),
                Duration.of(1, DAYS));
        manager.createNewEpic(epic);
        final Status statusEpicWithoutSubtask = epic.getStatus();
        //пустой список подзадач
        assertEquals(Status.NEW, statusEpicWithoutSubtask, "Неверный статус эпика - 'NEW'");
        manager.createNewSubtask(subtaskNew);
        subtaskNew.setEpicId(epic.getIdTask());
        epic.getListSubtask().put(subtaskNew.getIdTask(), subtaskNew);
        final Status statusEpicWithSubtaskNew = epic.getStatus();
        //подзадача со статусом NEW
        assertEquals(Status.NEW, statusEpicWithSubtaskNew, "Неверный статус эпика - subtask 'NEW'");
        epic.getListSubtask().clear();
        manager.createNewSubtask(subtaskDone);
        subtaskDone.setEpicId(epic.getIdTask());
        epic.getListSubtask().put(subtaskDone.getIdTask(), subtaskDone);
        final Status statusEpicWithSubtaskDone = epic.getStatus();
        //задача со статусом DONE
        assertEquals(Status.DONE, statusEpicWithSubtaskDone, "Неверный статус эпика - subtask 'DONE'");
        epic.getListSubtask().put(subtaskNew.getIdTask(), subtaskNew);
        final Status statusEpicWithSubtaskNewAndDone = epic.getStatus();
        //подзадача со статусом NEW и DONE
        assertEquals(Status.IN_PROGRESS, statusEpicWithSubtaskNewAndDone,
                "Неверный статус эпика - subtask 'NEW and DONE'");
        epic.getListSubtask().clear();
        manager.createNewSubtask(subtaskInProgress);
        epic.getListSubtask().put(subtaskInProgress.getEpicId(), subtaskInProgress);
        final Status statusEpicWithSubtaskInProgress = epic.getStatus();
        //подзадача со статусом IN_PROGRESS
        assertEquals(Status.IN_PROGRESS, statusEpicWithSubtaskInProgress,
                "Неверный статус эпика - subtask 'NEW and DONE'");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.of(1999, 2, 1, 1, 0), Duration.of(2, DAYS));
        manager.createNewTask(task);
        final Task updateTask = new Task("Test updateNewTask", "Test updateTask description", 0,
                Status.DONE, LocalDateTime.of(1999, 2, 1, 1, 0), Duration
                .of(2, DAYS));
        manager.updateTask(updateTask);
        assertNotNull(manager.getTasks().get(0), "Задачи нет");
        assertEquals(updateTask, manager.getTasks().get(0), "Задачи не совпадают " + updateTask.getIdTask());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        manager.createNewSubtask(subtask);
        final Subtask updateSubtask = new Subtask("Test UpdateNewSubtask",
                "Test UpdateSubtask description", 1, Status.IN_PROGRESS,
                epic.getIdTask(), LocalDateTime.now(), Duration.of(4, DAYS));
        manager.updateSubtask(updateSubtask, epic);
        assertNotNull(manager.getSubtasks().get(1), "Подзадачи нет");
        assertEquals(Status.IN_PROGRESS, manager.getEpics().get(0).getStatus(), "Эпики не совпадают");
        assertEquals(updateSubtask, manager.getSubtasks().get(1), "Подзадача не совпадает");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime
                .of(2000, 3, 8, 12, 0), Duration.of(2, DAYS));
        //эпик без подзадач(NEW статус)
        assertNotNull(manager.getEpics().get(0), "Эпика нет");
        assertEquals(epic.getStatus(), manager.getEpics().get(0).getStatus(), "Статус не совпадает");
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        epic.getListSubtask().put(subtask1.getEpicId(), subtask1);
        epic.getListSubtask().put(subtask2.getEpicId(), subtask2);
        //NEW статус
        assertNotNull(manager.getEpics().get(0), "Эпика нет");
        assertEquals(epic.getStatus(), manager.getEpics().get(0).getStatus(), "Статус не совпадает");
        Subtask updateSubtask1 = new Subtask("Test UpdateNewSubtask",
                "Test UpdateSubtask description", 0, Status.DONE, epic.getIdTask(), LocalDateTime.now(),
                Duration.of(4, DAYS));
        manager.updateSubtask(updateSubtask1, epic);
        //IN_PROGRESS статус
        assertNotNull(manager.getEpics().get(0), "Эпика нет");
        assertEquals(epic.getStatus(), manager.getEpics().get(0).getStatus(), "Статус не совпадает");
        Subtask updateSubtask2 = new Subtask("Test UpdateNewSubtask",
                "Test UpdateSubtask description", 0, Status.IN_PROGRESS,
                epic.getIdTask(), LocalDateTime.now(), Duration.of(4, DAYS));
        manager.updateSubtask(updateSubtask2, epic);
        assertNotNull(manager.getEpics().get(0), "Эпика нет");
        assertEquals(epic.getStatus(), manager.getEpics().get(0).getStatus(), "Статус не совпадает");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.now(), Duration.of(2, DAYS));
        //Не создана задача
        assertEquals(0, manager.getTasks().size(), "Задача создана!");
        manager.createNewTask(task);
        //Создана задача
        assertNotNull(manager.getTaskById(0), "Задача не создана!");
        assertNotNull(manager.history().get(0), "Задачи нет в истории!");
        assertEquals(task, manager.getTaskById(0), "Задачи не совпадают!");
        assertEquals(task, manager.history().get(0),
                "Задачи из истории не совпадают!");
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 0,
                Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        assertEquals(0, manager.getSubtasks().size(), "Подзадача создана!");
        manager.createNewSubtask(subtask1);
        //Создана задача
        assertNotNull(manager.getSubtaskById(1), "Подзадача не создана!");
        assertNotNull(manager.history().get(0), "Подзадачи нет в истории!");
        assertEquals(subtask1, manager.getSubtaskById(1), "Задачи не совпадают!");
        assertEquals(subtask1, manager.history().get(0),
                "Подзадача из истории не совпадают!");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        //Не создан эпик
        assertEquals(0, manager.getEpics().size(), "Эпик создан!");
        manager.createNewEpic(epic);
        //создан без подзадач
        assertEquals(0, manager.getEpicById(0).getListSubtask().size(), "Эпик не пустой");
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        manager.createNewSubtask(subtask1);
        manager.getEpicById(0).getListSubtask().put(subtask1.getIdTask(), subtask1);
        //Создана подзадача
        assertEquals(1, manager.getEpicById(0).getListSubtask().size(), "Подзадача не создана!");
        assertNotNull(manager.history().get(0), "Эпика нет в истории!");
        assertEquals(epic, manager.getEpicById(0), "Задачи не совпадают!");
        assertEquals(epic, manager.history().get(0), "Задачи из истории не совпадают!");
        assertEquals(epic.getStatus(), manager.getEpicById(0).getStatus(), "Статус не совпадает");
        assertEquals(epic.getStatus(), manager.history().get(0).getStatus(), "Cтатус не совпадает");
        Subtask subtask2 = new Subtask("Test NewSubtask2",
                "Test UpdateSubtask description", 2, Status.DONE,
                epic.getIdTask(), LocalDateTime.now(), Duration.of(4, DAYS));
        manager.createNewSubtask(subtask2);
        manager.getEpicById(0).getListSubtask().put(subtask2.getIdTask(), subtask2);
        assertEquals(epic.getStatus(), manager.getEpicById(0).getStatus(), "Статус не совпадает");
        assertEquals(epic.getStatus(), manager.history().get(0).getStatus(), "Cтатус не совпадает");
        Subtask update = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                1, Status.DONE, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        manager.updateSubtask(update, epic);
        assertEquals(epic.getStatus(), manager.getEpicById(0).getStatus(), "Статус не совпадает");
        assertEquals(epic.getStatus(), manager.history().get(0).getStatus(), "Cтатус не совпадает");
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.now(), Duration.of(2, DAYS));
        manager.removeTaskById(0);
        manager.createNewTask(task);
        assertNotNull(manager.getTasks().get(0), "Задача не создана");
        manager.removeTaskById(0);
        assertEquals(0, manager.getTasks().size(), "Задача не была удалена");
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                1, Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        manager.removeSubtaskById(1);
        manager.createNewSubtask(subtask1);
        epic.getListSubtask().put(subtask1.getIdTask(), subtask1);
        assertNotNull(manager.getSubtasks().get(1), "Подзадача не создана!");
        manager.removeSubtaskById(1);
        assertEquals(0, epic.getListSubtask().size());
        assertEquals(0, manager.getSubtasks().size(), "Подзадача не была удалена");
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.removeEpicById(0);
        manager.createNewEpic(epic);
        assertNotNull(manager.getEpics().get(0), "Эпик не создан!");
        assertEquals(Status.NEW, manager.getEpics().get(0).getStatus(), "Статус эпика не совпадает");
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                0, Status.IN_PROGRESS, epic.getIdTask(), LocalDateTime.now(), Duration.of(2, DAYS));
        manager.createNewSubtask(subtask1);
        epic.getListSubtask().put(subtask1.getIdTask(), subtask1);
        assertEquals(Status.IN_PROGRESS, manager.getEpics().get(0).getStatus(), "Статусы эпика не совпадают");
        manager.removeEpicById(0);
        assertEquals(0, manager.getSubtasks().size(), "Подзадача не была удалена");
        assertEquals(0, manager.getEpics().size(), "Эпик не был удален");
    }

    @Test
    void clearTask() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.of(1999, 2, 1, 1, 0), Duration.of(2, DAYS));
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", 0, Status.NEW,
                LocalDateTime.now(), Duration.of(2, DAYS));
        assertEquals(0, manager.getTasks().size(), "Задача создана!");
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        assertNotNull(manager.getTasks().get(0), "Задача1 не создана");
        assertNotNull(manager.getTasks().get(1), "Задача2 не создана");
        assertEquals(2, manager.getTasks().size(), "Задачи не созданы");
        manager.clearTask();
        assertEquals(0, manager.getTasks().size(), "Задачи не была удалена");
    }

    @Test
    void clearSubtask() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime
                .of(1999, 2, 1, 1, 0), Duration.of(2, DAYS));
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description",
                0, Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.of(3, DAYS));
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        epic.getListSubtask().put(subtask1.getIdTask(), subtask1);
        epic.getListSubtask().put(subtask2.getIdTask(), subtask2);
        assertEquals(2, manager.getSubtasks().size(), "Подзадача не создана!");
        manager.clearSubtask();
        assertEquals(0, epic.getListSubtask().size());
        assertEquals(0, manager.getSubtasks().size(), "Подзадачи не были удалены");
    }

    @Test
    void clearEpic() {
        Epic epic = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                0, Status.DONE, epic.getIdTask(), LocalDateTime
                .of(1999, 2, 1, 1, 0), Duration.of(2, DAYS));
        Epic epic2 = new Epic("Test name", "Test description", Status.NEW);
        manager.createNewSubtask(subtask1);
        manager.createNewEpic(epic2);
        epic.getListSubtask().put(subtask1.getIdTask(), subtask1);
        assertEquals(2, manager.getEpics().size(), "Эпики не создана!");
        manager.clearEpic();
        assertEquals(0, manager.getSubtasks().size(), "Подзадачи не были удалены");
        assertEquals(0, manager.getEpics().size(), "Эпики не были удалены");
    }
}