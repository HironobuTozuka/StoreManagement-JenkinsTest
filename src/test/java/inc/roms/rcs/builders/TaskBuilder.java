package inc.roms.rcs.builders;

import inc.roms.rcs.service.task.domain.model.FailReason;
import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskStatus;

public abstract class TaskBuilder<T extends Task> {

    protected TaskStatus taskStatus;
    protected String taskId;
    protected FailReason failReason;

    public TaskBuilder<T> taskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    public TaskBuilder<T> taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public abstract T build();

    public static PickBuilder pick() {
        return new PickBuilder();
    }

    public TaskBuilder<T> failReason(FailReason failReason) {
        this.failReason = failReason;
        return this;
    }
}
