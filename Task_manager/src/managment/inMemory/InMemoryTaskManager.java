package managment.inMemory;

import managment.HistoryManager;
import managment.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    Comparator<Task> userComparator = (task1, task2) -> {
        if (task1.getIdTask() == task2.getIdTask()) {
            return 0;
        }
        if (task1.getStartTime() != null && task2.getStartTime() != null &&
                task1.getStartTime().equals(task2.getStartTime())) {
            return 1;
        }
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else {
            return 1;
        }
    };
    private int idCount = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = new InMemoryHistoryManager();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public int createNewId() {
        return idCount++;
    }

    public void setTasks(Map<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public void setSubtasks(Map<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setEpics(Map<Integer, Epic> epics) {
        this.epics = epics;
    }

    public boolean validCreateTask(Task task) {
        Set<Task> sortTaskList = getPrioritizedTasks();
        for (Task oneTask : sortTaskList) {
            if (task.getStartTime() != null && oneTask.getStartTime() != null &&
                    task.getStartTime().equals(oneTask.getStartTime())) {

                System.out.println("Задача с таким же начальным временем уже есть: " + oneTask.getNameOfTask());
                return false;

            } else if (task.getStartTime() != null && oneTask.getGetEndTime() != null &&
                    task.getStartTime().isBefore(oneTask.getGetEndTime())) {
                if (task.getGetEndTime() != null && oneTask.getStartTime() != null &&
                        task.getGetEndTime().isBefore(oneTask.getStartTime())) {
                    return true;
                }
                System.out.println("Время заданной задачи пересекается с :" + oneTask.getNameOfTask());
                return false;
            } else if (task.getGetEndTime() != null && oneTask.getStartTime() != null &&
                    task.getGetEndTime().equals(oneTask.getStartTime())) {
                System.out.println("Конечное время совпадает с :" + oneTask.getNameOfTask());
                return false;
            }
        }
        return true;
    }

    public boolean validUpdateTask(Task task) {
        Set<Task> sortTaskList = getPrioritizedTasks();
        for (Task oneTask : sortTaskList) {
            if (task.getStartTime() != null && oneTask.getStartTime() != null &&
                    task.getStartTime().equals(oneTask.getStartTime())) {
                if (task.getIdTask() == oneTask.getIdTask()) {
                    System.out.println("Одинаковая задача");
                    return true;
                }
                System.out.println("Задача с таким же начальным временем уже есть: " + oneTask.getNameOfTask());
                return false;
            } else if (task.getStartTime() != null && oneTask.getGetEndTime() != null &&
                    task.getStartTime().isBefore(oneTask.getGetEndTime())) {
                if (task.getGetEndTime() != null && oneTask.getStartTime() != null &&
                        task.getGetEndTime().isBefore(oneTask.getStartTime())) {
                    return true;
                }
                if (task.getIdTask() == oneTask.getIdTask()) {
                    return true;
                }
                System.out.println("Время заданной задачи пересекается с :" + oneTask.getNameOfTask());
                return false;
            } else if (task.getGetEndTime() != null && oneTask.getStartTime() != null &&
                    task.getGetEndTime().equals(oneTask.getStartTime())) {
                if (task.getIdTask() == oneTask.getIdTask()) {
                    return true;
                }
                System.out.println("Конечное время совпадает с :" + oneTask.getNameOfTask());
                return false;
            }
        }
        return true;
    }

    //создание задач
    @Override
    public void createNewTask(Task task) {
        if (validCreateTask(task)) {
            task.setIdTask(createNewId());
            tasks.put(task.getIdTask(), task);
        }

    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        if (validCreateTask(subtask) && getEpicById(subtask.getEpicId()) != null) {
            subtask.setIdTask(createNewId());
            getEpicById(subtask.getEpicId()).getListSubtask().put(subtask.getIdTask(),subtask);
            subtasks.put(subtask.getIdTask(), subtask);
        }


    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setIdTask(createNewId());
        epics.put(epic.getIdTask(), epic);


    }
    //конец создания задач

    //обновление задач
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getIdTask())) {
            System.out.println("Ошибка! Такой задачи не существует");
            return;
        }
        if (validUpdateTask(task)) {
            tasks.put(task.getIdTask(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, Epic epic) {
        if (!subtasks.containsKey(subtask.getIdTask())) {
            System.out.println("Ошибка! Такой задачи не существует");
            return;
        }
        if (validUpdateTask(subtask)) {
            subtasks.put(subtask.getIdTask(), subtask);
            epic.getListSubtask().put(subtask.getIdTask(), subtask);
            epic.getStatus();
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        if (!tasks.containsKey(epic.getIdTask())) {
            System.out.println("Ошибка! Такой задачи не существует");
            return;
        }
        epics.put(epic.getIdTask(), epic);
    }

    //конец обновления задач
    //получение задач по ID;
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);

    }
    //конец получения задач по ID

    //удаление по ID
    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Ошибка! Такой задачи не существует");
            return;
        }
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Ошибка! Такой подзадачи не существует");
            return;
        }
        historyManager.remove(id);
        subtasks.remove(id);
        getEpicById(getSubtaskById(id).getEpicId()).getListSubtask().remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Ошибка! Такого эпика не существует");
            return;
        }
        Epic epic = epics.get(id);
        if (epic.getListSubtask().size() > 0) {
            for (Integer subtaskID : epic.getListSubtask().keySet()) {
                historyManager.remove(subtaskID);
                subtasks.remove(subtaskID);
            }
        }

        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void clearTask() {
        tasks.clear();
    }

    @Override
    public void clearSubtask() {
        for (int i = 0; i < epics.size(); i++) {
            epics.get(i).getListSubtask().clear();
        }

        subtasks.clear();

    }

    @Override
    public void clearEpic() {
        epics.clear();
        subtasks.clear();

    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        List<Task> mapAllTask = new ArrayList<>();
        for (Integer keyMap : getTasks().keySet()) {
            mapAllTask.add(getTasks().get(keyMap));
        }
        for (Integer keyMap : getSubtasks().keySet()) {
            mapAllTask.add(getSubtasks().get(keyMap));
        }
        for (Integer keyMap : getEpics().keySet()) {

            mapAllTask.add(getEpics().get(keyMap));
        }
        Set<Task> treeTask = new TreeSet<>(userComparator);
        for (int i = 0; i < mapAllTask.size(); i++) {
            treeTask.add(mapAllTask.get(i));
        }
        return treeTask;
    }


}


