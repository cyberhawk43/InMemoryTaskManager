package test.fp6;

import managment.HistoryManager;
import managment.inMemory.InMemoryHistoryManager;
import managment.Managers;
import managment.TaskManager;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = new InMemoryHistoryManager();
    Task firstTask = new Task("Task1", "Desc Task1", 0, Status.NEW, LocalDateTime
            .of(2000, 2, 24, 12, 0),
            Duration.of(1, DAYS));

    HistoryManagerTest() throws IOException {
    }


    @Test
    void addAndRemove() {
        taskManager.createNewTask(firstTask);
        final List<Task> emptyHistory = historyManager.getHistory();
        //пустой
        assertNotNull(emptyHistory, "История задач не пустая");
        assertEquals(0, historyManager.getHistory().size(), "История не пустая");
        historyManager.add(firstTask);
        final List<Task> history = historyManager.getHistory();
        //не пустой
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
        historyManager.add(firstTask);
        //дублирование
        assertEquals(1, history.size(), "Дублирование неверно");
        history.remove(0);
        //удаление
        assertEquals(0, history.size(), "Задача не удалена");
    }
}