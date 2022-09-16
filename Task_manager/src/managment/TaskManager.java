package managment;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public interface TaskManager {

    Map<Integer, Task> getTasks();
    Map<Integer, Subtask> getSubtasks();
    Map<Integer, Epic> getEpics();

    List<Task> history();

    int createNewId();

    //создание задач
    void createNewTask(Task task);

    void createNewSubtask(Subtask subtask);

    void createNewEpic(Epic epic);
    //конец создания задач

    //обновление задач
    void updateTask(Task task);

    void updateSubtask(Subtask subtask, Epic epic);

    void updateEpic(Epic epic);
    //конец обновления задач


    //получение задач по ID;
    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);
    //конец получения задач по ID
    Set<Task> getPrioritizedTasks();

    //удаление по ID
    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void clearTask();

    void clearSubtask();

    void clearEpic();
}
