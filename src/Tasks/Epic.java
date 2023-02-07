package Tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(Integer id, String name, TaskStatus status, String description) {
        super(id, name, status, description, 0L, LocalDateTime.now());
        endTime = startTime;
    }

    public Epic(Integer id, String name, TaskStatus status, String description, long duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
        endTime = startTime;
    }

    public Epic(String name, TaskStatus status, String description, long duration, LocalDateTime startTime) {
        super(name, status, description, duration, startTime);
        endTime = startTime;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public List<Integer> getSubtasksIds() {
        return subtaskIds;
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                "} " + super.toString();
    }
}
