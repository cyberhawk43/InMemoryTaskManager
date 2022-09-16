package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String nameOfTask, String descriptionTask, int id, Status statusTask, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(nameOfTask, descriptionTask, id, statusTask, startTime, duration);
        super.setTaskType(TaskType.SUBTASK);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + super.getNameOfTask() + '\'' +
                ", id='" + super.getIdTask() + '\'' +
                ", description=" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", type=" + getTaskType() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration().toDays() +
                ", getEndTime=" + super.getGetEndTime() +
                ", epicId=" + getEpicId() +
                '}' + "\n";
    }

}
