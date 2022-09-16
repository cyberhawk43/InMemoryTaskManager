package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private int id;
    private TaskType type;
    private String name;
    private task.Status status;
    private String description;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected LocalDateTime getEndTime;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        startTime = null;
        duration = Duration.ZERO;


    }

    public Task(String name, String description,int id, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.type = TaskType.TASK;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.getEndTime = getStartTime().plus(getDuration());

    }

    public void setTaskType(TaskType type) {
        this.type = type;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setGetEndTime(LocalDateTime getEndTime) {
        this.getEndTime = getEndTime;
    }


    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getGetEndTime() {
        return getEndTime;
    }

    public String getNameOfTask() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIdTask() {
        return id;
    }

    public void setIdTask(int idTask) {
        this.id = idTask;
    }

    public task.Status getStatus() {
        return status;
    }

    public void setStatus(task.Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm");
    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", description=" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime.format(FORMATTER) +
                ", duration=" + duration.toDays() +
                ", getEndTime=" + getEndTime.format(FORMATTER) +
                ", type=" + getTaskType() +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false; // можно оформить и так
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) &&
                (id == otherTask.id) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(startTime.format(FORMATTER), otherTask.startTime.format(FORMATTER)) &&
                Objects.equals(duration, otherTask.duration) &&
                Objects.equals(getEndTime.format(FORMATTER), otherTask.getEndTime.format(FORMATTER));
    }
}
