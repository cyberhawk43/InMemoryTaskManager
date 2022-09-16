package test.fp6;

import managment.Managers;
import managment.TaskManager;

import managment.file.FileBackedTasksManager;
import task.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager = new FileBackedTasksManager("Test3.CSV");


    @Test
    void statusEpic() {
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
        taskManager.createNewEpic(epic);
        final Status statusEpicWithoutSubtask = epic.getStatus();
        //пустой список подзадач
        assertEquals(Status.NEW, statusEpicWithoutSubtask, "Неверный статус эпика - 'NEW'");
        taskManager.createNewSubtask(subtaskNew);
        subtaskNew.setEpicId(epic.getIdTask());
        epic.getListSubtask().put(subtaskNew.getIdTask(), subtaskNew);
        final Status statusEpicWithSubtaskNew = epic.getStatus();
        //подзадача со статусом NEW
        assertEquals(Status.NEW, statusEpicWithSubtaskNew, "Неверный статус эпика - subtask 'NEW'");
        epic.getListSubtask().clear();
        taskManager.createNewSubtask(subtaskDone);
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
        taskManager.createNewSubtask(subtaskInProgress);
        epic.getListSubtask().put(subtaskInProgress.getEpicId(), subtaskInProgress);
        final Status statusEpicWithSubtaskInProgress = epic.getStatus();
        //подзадача со статусом IN_PROGRESS
        assertEquals(Status.IN_PROGRESS, statusEpicWithSubtaskInProgress,
                "Неверный статус эпика - subtask 'NEW and DONE'");
    }

}