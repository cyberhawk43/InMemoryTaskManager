package task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class Epic extends Task {


    transient Map<Integer, Subtask> listSubtask = new HashMap<>();

    public Epic(String name, String description, Status statusTask) {
        super(name, description, statusTask);
        super.setTaskType(TaskType.EPIC);
        setStatus(statusTask);
        getStatus();
        getGetEndTime();

    }

    public Map<Integer, Subtask> getListSubtask() {
        return listSubtask;
    }



    @Override
    public Status getStatus() {
        Map<Integer, Subtask> subtasks = getListSubtask();
        Map<Status, Integer> statusCounter = new HashMap<>();
        for (Subtask subtask : subtasks.values()) {
            Integer statusCount = statusCounter.getOrDefault(subtask.getStatus(), 0);
            statusCounter.put(subtask.getStatus(), statusCount + 1);
        }
        if (subtasks.size() == 0 || statusCounter.getOrDefault(Status.NEW, 0) == subtasks.size()) {
            super.setStatus(Status.NEW);
        } else if (statusCounter.getOrDefault(Status.DONE, 0) == subtasks.size()) {
            super.setStatus(Status.DONE);
        } else {
            super.setStatus(Status.IN_PROGRESS);
        }
        return super.getStatus();
    }

    @Override
    public int getIdTask() {
        return super.getIdTask();
    }


    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }



    @Override
    public LocalDateTime getGetEndTime() {
        Map<Integer, Subtask> subtasks = getListSubtask();
        List<LocalDateTime>  localDateTimes = new ArrayList<>();

        if(subtasks.size()>0){
            for(Subtask subtask: subtasks.values()){
                localDateTimes.add(subtask.getGetEndTime());
            }
            LocalDateTime endTime = localDateTimes.get(0);
            for(int i = 0; i<localDateTimes.size();i++){
                if(endTime.isBefore(localDateTimes.get(i))){
                    endTime = localDateTimes.get(i);
                }
            }
            super.setGetEndTime(endTime);
        } else {
            super.setGetEndTime(null);
        }
        return super.getGetEndTime();


    }
    @Override
    public LocalDateTime getStartTime() {
        Map<Integer, Subtask> subtasks = getListSubtask();
        List<LocalDateTime> localDateTimes = new ArrayList<>();

        if(subtasks.size()>0){
            for(Subtask subtask: subtasks.values()){
                localDateTimes.add(subtask.getStartTime());
            }
            LocalDateTime startTime= localDateTimes.get(0);
            for(int i = 0; i<localDateTimes.size();i++){
                if(startTime.isAfter(localDateTimes.get(i))){
                    startTime = localDateTimes.get(i);
                }
            }
            super.setStartTime(startTime);
            return super.getStartTime();
        } else {
            super.setStartTime(null);
            return super.getStartTime();
        }
    }

    @Override
    public Duration getDuration() {
        Map<Integer, Subtask> subtasks = getListSubtask();
        List<Duration> durationList = new ArrayList<>();

        if(subtasks.size()>0){
            for(Subtask subtask: subtasks.values()){
                durationList.add(subtask.getDuration());
            }
            Duration duration = Duration.ZERO;
            for(int i = 0; i<durationList.size();i++){
                    duration = duration.plus(durationList.get(i));

            }

            super.setDuration(duration);


        }
        return super.getDuration();
    }



    @Override
    public String toString() {
        return "Epic{" +
                "name=' " + super.getNameOfTask() + '\'' +
                ", id='" + super.getIdTask() + '\'' +
                ", description=" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration().toDays() +
                ", getEndTime=" + super.getGetEndTime() +
                ", type=" + getTaskType() +
                ", \n listSubtask="  +
                '}' + "\n";
    }
}
