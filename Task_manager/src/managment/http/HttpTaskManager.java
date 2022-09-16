package managment.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import managment.file.FileBackedTasksManager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import task.adapter.Adapter;


import java.util.HashMap;

import java.util.Map;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvClient;
    Gson gson = Adapter.allRegister(new GsonBuilder());

    public HttpTaskManager(String url) {
        super();
        this.kvClient = new KVTaskClient(url);


    }


    @Override
    public void save() {
        super.save();

        Map<Integer, Task> allTasks = new HashMap<>();
        Map<Integer, Subtask> allSubtasks = new HashMap<>();
        Map<Integer, Epic> allEpics = new HashMap<>();
        for (Task task : super.getTasks().values()) {
            allTasks.put(task.getIdTask(), task);
        }
        for (Subtask subtask : super.getSubtasks().values()) {
            allSubtasks.put(subtask.getIdTask(), subtask);
        }
        for (Epic epic : super.getEpics().values()) {
            allEpics.put(epic.getIdTask(), epic);
        }
        if (allTasks.size() > 0) {
            String json = gson.toJson(allTasks);
            kvClient.put("TASK", json);
        }
        if (allSubtasks.size() > 0) {
            String json = gson.toJson(allSubtasks);
            kvClient.put("SUBTASK", json);
        }
        if (allEpics.size() > 0) {
            String json = gson.toJson(allEpics);
            kvClient.put("EPIC", json);
        }

    }


    public void load() {
        String bodyTask = kvClient.load("TASK");
        String bodySubtask = kvClient.load("SUBTASK");
        String bodyEpic = kvClient.load("EPIC");
        if (!bodyTask.equals("null")) {
            Map<Integer, Task> tasks = gson.fromJson(bodyTask, new TypeToken<HashMap<Integer, Task>>() {
            }.getType());
            if (tasks.size() > 0) {
                for (Task task : tasks.values()) {
                    getTasks().put(task.getIdTask(), task);
                }
            }
        }
        if (!bodyEpic.equals("null")) {
            Map<Integer, Epic> epics = gson.fromJson(bodyEpic, new TypeToken<HashMap<Integer, Epic>>() {
            }.getType());
            if (epics.size() > 0) {
                for (Epic epic : epics.values()) {
                    getEpics().put(epic.getIdTask(), new Epic(epic.getNameOfTask(), epic.getDescription(), Status.NEW));
                    getEpicById(epic.getIdTask()).setIdTask(epic.getIdTask());
                }
            }

        }
        if (!bodySubtask.equals("null")) {
            Map<Integer, Subtask> subtasks = gson.fromJson(bodySubtask, new TypeToken<HashMap<Integer, Subtask>>() {
            }.getType());
            if (subtasks.size() > 0) {
                for (Subtask subtask : subtasks.values()) {
                    getSubtasks().put(subtask.getIdTask(), subtask);
                    getEpicById(subtask.getEpicId()).getListSubtask().put(subtask.getIdTask(), subtask);
                    updateEpic((getEpicById(subtask.getEpicId())));
                }
            }

        }


    }
}
