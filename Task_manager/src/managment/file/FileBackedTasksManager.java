package managment.file;

import managment.ManagerSaveException;
import managment.inMemory.InMemoryTaskManager;
import task.*;


import java.io.*;

import java.time.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;


public class FileBackedTasksManager extends InMemoryTaskManager {

    List<Integer> listHistoryID = new ArrayList<>();

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
    private String nameFile;

    public FileBackedTasksManager(String nameFile) {
        this.nameFile = nameFile;
    }

    public FileBackedTasksManager() {
    }


    public void save() {
        List<Task> mapAllTask;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Test3.CSV"))) {
            bw.write("id,type,name,status,description,epic, startTime, duration, getEndTime" + "\n");
            mapAllTask = new ArrayList<>();
            for (Integer keyMap : super.getTasks().keySet()) {
                mapAllTask.add(super.getTasks().get(keyMap));
            }
            for (Integer keyMap : super.getSubtasks().keySet()) {
                mapAllTask.add(super.getSubtasks().get(keyMap));
            }
            for (Integer keyMap : super.getEpics().keySet()) {

                mapAllTask.add(super.getEpics().get(keyMap));
            }
            if (mapAllTask.size() > 0) {
                for (int i = 0; i < mapAllTask.size(); i++) {
                    if (i != (mapAllTask.size() - 1)) {
                        bw.write(toString(mapAllTask.get(i)));
                        bw.write(",\n");
                    } else {
                        bw.write(toString(mapAllTask.get(i)));
                    }
                }
            }
            bw.write("\n");
            bw.write("\n");
            if (listHistoryID.size() > 0) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; listHistoryID.size() > i; i++) {
                    sb.append(listHistoryID.get(i) + ",");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    bw.write(sb.toString());
                }
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }


    public String toString(Task task) {
        String taskToString = "";
        if (task.getTaskType().equals(TaskType.SUBTASK) && task.getTaskType() != null) {
            Subtask subtask = (Subtask) task;
            taskToString = subtask.getIdTask() + "," + subtask.getTaskType() + "," + subtask.getNameOfTask() + "," +
                    subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId() + "," +
                    subtask.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm")) +
                    "," + subtask.getDuration().toDays() + "," +
                    subtask.getGetEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm"));
        } else if (task.getTaskType().equals(TaskType.TASK) && task.getTaskType() != null) {
            taskToString = task.getIdTask() + "," + task.getTaskType() + "," + task.getNameOfTask() + "," +
                    task.getStatus() + "," + task.getDescription() + "," +
                    task.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm")) + "," +
                    task.getDuration().toDays() + "," +
                    task.getGetEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm"));
        } else if (task.getStartTime() != null && task.getGetEndTime() != null) {
            taskToString = task.getIdTask() + "," + task.getTaskType() + "," + task.getNameOfTask() + "," +
                    task.getStatus() + "," + task.getDescription() + "," +
                    task.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm")) + "," +
                    task.getDuration().toDays() + "," +
                    task.getGetEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH : mm"));
        } else if (task.getStartTime() == null && task.getGetEndTime() == null) {
            taskToString = task.getIdTask() + "," + task.getTaskType() + "," + task.getNameOfTask() + "," +
                    task.getStatus() + "," + task.getDescription() + "," +
                    Optional.empty() + "," +
                    task.getDuration().toDays() + "," +
                    Optional.empty();

        }
        return taskToString;
    }

    protected Map<Integer, Task> fromFileToTask() {
        String nameFile = getNameFile();
        List<String> tasksFromFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            while (br.ready()) {
                tasksFromFile.add(br.readLine());
            }
            DateTimeFormatter formatter = ofPattern("dd.MM.yyyy|HH : mm");

            for (int i = 1; i < (tasksFromFile.size() - 2); i++) {
                String[] split = tasksFromFile.get(i).split(",");
                Status status;
                if (split[3].equals("NEW")) {
                    status = Status.NEW;
                } else if (split[3].equals("IN_PROGRESS")) {
                    status = Status.IN_PROGRESS;
                } else {
                    status = Status.DONE;
                }

                if (split[1].equals("TASK")) {
                    Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), status,
                            LocalDateTime.parse(split[5], formatter),
                            Duration.of(Integer.parseInt(split[6]), DAYS));

                    super.getTasks().put(Integer.parseInt(split[0]), task);
                } else if (split[1].equals("SUBTASK")) {
                    Subtask subtask = new Subtask(split[2], split[4], Integer.parseInt(split[0]), status, Integer.parseInt(split[5]),
                            LocalDateTime.parse(split[6], formatter), Duration.of(1, DAYS));
                    subtask.setIdTask(Integer.parseInt(split[0]));
                    super.getSubtasks().put(Integer.parseInt(split[0]), subtask);
                } else if (split[1].equals("EPIC") && !(split[5].equals("Optional.empty"))) {
                    Epic epic = new Epic(split[2], split[4], status);
                    epic.setIdTask(Integer.parseInt(split[0]));
                    epic.setStartTime(LocalDateTime.parse(split[5], formatter));
                    epic.setGetEndTime(LocalDateTime.parse(split[7], formatter));
                    epic.setDuration(Duration.of(Integer.parseInt(split[6]), DAYS));
                    super.getEpics().put(Integer.parseInt(split[0]), epic);
                } else {
                    Epic epic = new Epic(split[2], split[4], status);
                    epic.setIdTask(Integer.parseInt(split[0]));
                    super.getEpics().put(Integer.parseInt(split[0]), epic);
                }
            }


        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return super.getTasks();
    }

    protected List<Task> fromFileToHistory(String nameFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            List<String> history = new ArrayList<>();
            while (br.ready()) {
                history.add(br.readLine());
            }
            String[] historyID = history.get(history.size() - 1).split(",");
            for (int i = 0; i < historyID.length; i++) {
                if (getTasks().containsKey(Integer.parseInt(historyID[i]))) {
                    super.getTaskById(Integer.parseInt(historyID[i]));
                } else if (getSubtasks().containsKey(Integer.parseInt(historyID[i]))) {
                    super.getSubtaskById(Integer.parseInt(historyID[i]));
                } else if (getEpics().containsKey(Integer.parseInt(historyID[i]))) {
                    super.getEpicById(Integer.parseInt(historyID[i]));
                }
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка считывания истории");
        }
        return super.history();
    }


    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }


    @Override
    public Task getTaskById(int id) {
        listHistoryID.add(id);
        save();
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        listHistoryID.add(id);
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        listHistoryID.add(id);
        save();
        return super.getEpicById(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Epic epic) {
        super.updateSubtask(subtask, epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }


    static FileBackedTasksManager loadFromFile() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("Test.csv");
        String nameFile = fileBackedTasksManager.getNameFile();
        // если правильно понял, то при восстановлении задач обращаюсь к файлу один раз не считая истории.
        // Объеденил все в один метод.
        fileBackedTasksManager.fromFileToTask();
        fileBackedTasksManager.fromFileToHistory(nameFile);
        return fileBackedTasksManager;
    }

    public String getNameFile() {
        return nameFile;
    }

    public Set<Task> getPrioritizedTasks() {

        List<Task> mapAllTask = new ArrayList<>();
        for (Integer keyMap : super.getTasks().keySet()) {
            mapAllTask.add(super.getTasks().get(keyMap));
        }
        for (Integer keyMap : super.getSubtasks().keySet()) {
            mapAllTask.add(super.getSubtasks().get(keyMap));
        }
        for (Integer keyMap : super.getEpics().keySet()) {

            mapAllTask.add(super.getEpics().get(keyMap));
        }
        Set<Task> treeTask = new TreeSet<>(userComparator);
        for (int i = 0; i < mapAllTask.size(); i++) {
            treeTask.add(mapAllTask.get(i));
        }
        return treeTask;

    }


    public static void main(String[] args) {


        FileBackedTasksManager fbtm = new FileBackedTasksManager("Test3.CSV");
        Task firstTask = new Task("Task1", "Desc Task1", 0, Status.NEW, LocalDateTime
                .of(2000, 2, 24, 12, 0),
                Duration.of(1, DAYS));
        Task secondTask = new Task("Task2", "Desc Task2", 0, Status.NEW, LocalDateTime
                .of(1999, 3, 25, 12, 0),
                Duration.of(1, DAYS));
        Epic firstEpic = new Epic("Epic1", "Desc Epic1", Status.NEW);
        Subtask firstSubtask = new Subtask("Subtask1", "Desc Subtask1"
                , 0, Status.DONE, firstEpic.getIdTask(), LocalDateTime
                .of(1997, 1, 12, 10, 10),
                Duration.of(1, DAYS));
        Subtask secondSubtask = new Subtask("Subtask2", "Desc Subtask2"
                , 0, Status.DONE, firstEpic.getIdTask(), LocalDateTime.of(1991, 1, 13, 11, 0)
                , Duration.of(1, DAYS));
        Subtask thirdSubtask = new Subtask("Subtask3", "Desc Subtask3"
                , 0, Status.NEW, firstEpic.getIdTask(), LocalDateTime.of(1996, 4, 18, 11, 0),
                Duration.of(1, DAYS));
        firstEpic = new Epic("Epic1", "Desc Epic1",
                Status.NEW);
        Epic secondEpic = new Epic("Epic2", "Desc Epic2", Status.NEW);

        secondEpic = new Epic("Epic2", "Desc Epic2",
                Status.NEW);


        fbtm.createNewTask(firstTask);
        fbtm.createNewTask(secondTask);
        fbtm.createNewEpic(firstEpic);
        fbtm.createNewSubtask(firstSubtask);
        fbtm.createNewSubtask(secondSubtask);
        fbtm.createNewSubtask(thirdSubtask);
        firstSubtask.setEpicId(firstEpic.getIdTask());
        secondSubtask.setEpicId(firstEpic.getIdTask());
        thirdSubtask.setEpicId(firstEpic.getIdTask());
        firstEpic.getListSubtask().put(firstSubtask.getIdTask(), firstSubtask);
        firstEpic.getListSubtask().put(secondSubtask.getIdTask(), secondSubtask);
        firstEpic.getListSubtask().put(thirdSubtask.getIdTask(), thirdSubtask);
        fbtm.createNewEpic(secondEpic);
        Task checkTask = new Task("checkTask", "Desc Task", 0, Status.NEW, LocalDateTime
                .of(1897, 2, 24, 12, 0),
                Duration.of(1, DAYS));
        fbtm.createNewTask(checkTask);
        System.out.println("айди - " + checkTask.getIdTask());
        fbtm.getTaskById(0);
        fbtm.getEpicById(2);


        fbtm.removeTaskById(1);


        //скопировал и переименовал файл чтоб восстановиться из него
        File dir = new File("C://Users/kudryavtsev/фп5");
        File file = new File(dir, "Test3.CSV");
        FileBackedTasksManager fileBack = loadFromFile();
        System.out.println("таски из файла \n" + fileBack.getTasks());
        System.out.println("сабтаски из файла \n" + fileBack.getSubtasks());
        System.out.println("эпики из файла \n" + fileBack.getEpics());
        System.out.println("История из файла\n " + fileBack.history());
        System.out.println("Приоритеты : \n" + fbtm.getPrioritizedTasks());
    }


}
