package inc.roms.rcs.service.task;

import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.listeners.TaskBundleListener;
import inc.roms.rcs.service.task.listeners.TaskUpdateListener;
import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.service.task.domain.repository.TaskRepository;
import inc.roms.rcs.vo.task.TaskId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskBundleService taskBundleService;
    private final List<TaskUpdateListener> taskUpdateListeners;
    private final List<TaskBundleListener> taskBundleListeners;
    private final TaskRepository taskRepository;

    @Transactional
    public void handleTaskUpdate(TaskUpdateRequest taskUpdateRequest) {
        Task task = updateTaskOnly(taskUpdateRequest);

        TaskBundle taskBundle = taskBundleService.findFirstByTasksContaining(task);

        if (updateTaskBundle(taskBundle)) {
            taskBundleListeners.forEach(listener -> listener.onUpdate(taskBundle));
        }
    }

    public boolean updateTaskBundle(TaskBundle bundle) {
        boolean wasStatusUpdated = bundle.updateStatus();
        bundle.setLastTaskUpdate(LocalDateTime.now());
        taskBundleService.save(bundle);
        return wasStatusUpdated;
    }

    public Task updateTaskOnly(TaskUpdateRequest taskUpdateRequest) {
        Task task = findByTaskId(TaskId.from(taskUpdateRequest.getTaskId()));
        if (updateTaskStatus(taskUpdateRequest, task))
            taskUpdateListeners.forEach(handler -> handler.onUpdate(task, taskUpdateRequest));
        return task;
    }

    private boolean updateTaskStatus(TaskUpdateRequest taskUpdateRequest, Task task) {
        log.info("Task to be updated: {}", task);
        if (task.getStatus().equals(taskUpdateRequest.getTaskStatus())) return false;

        task.setStatus(taskUpdateRequest.getTaskStatus());

        if(taskUpdateRequest.getDetails() != null) {
            task.setFailReason(taskUpdateRequest.getDetails().getFailReason());
        }

        taskRepository.save(task);
        return true;
    }

    public Task findByTaskId(TaskId taskId) {
        return taskRepository.findByTaskId(taskId);
    }
}
