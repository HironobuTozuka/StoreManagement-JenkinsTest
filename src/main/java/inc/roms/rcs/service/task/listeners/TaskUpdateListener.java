package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;

public abstract class TaskUpdateListener<T extends Task> {

    protected abstract Class<T> classOfInterest();

    private <R extends Task> boolean isListeningToUpdatesOf(Class<R> taskClass) {
        return classOfInterest().isAssignableFrom(taskClass);
    };

    protected abstract void onTaskUpdate(T task, TaskUpdateRequest taskUpdateRequest);

    public final void onUpdate(T task, TaskUpdateRequest taskUpdateRequest) {
        if(!isListeningToUpdatesOf(task.getClass())) return;

        onTaskUpdate(task, taskUpdateRequest);
    };
}
