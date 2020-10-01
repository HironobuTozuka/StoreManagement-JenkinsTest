package inc.roms.rcs.service.task.domain;

import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.service.task.domain.model.TaskBundleStatus;
import inc.roms.rcs.service.task.domain.repository.TaskBundleRepository;
import inc.roms.rcs.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static inc.roms.rcs.service.task.domain.model.TaskBundleType.DELIVERY;
import static inc.roms.rcs.service.task.domain.model.TaskBundleType.PICKING;

@Service
@RequiredArgsConstructor
public class TaskBundleService {
    private final TaskBundleRepository taskBundleRepository;

    public boolean isOngoingPicking() {
        List<TaskBundle> inProgress = findNotFinished(); //all not completed or failed
        return isAnyPicking(inProgress) || isAnyDelivery(inProgress);
    }

    private static boolean isAnyDelivery(List<TaskBundle> notFinished) {
        return notFinished.stream().anyMatch(it -> DELIVERY.equals(it.getType())); // no deliveres other than completed or failed
    }

    private static boolean isAnyPicking(List<TaskBundle> notFinished) {
        return notFinished.stream().anyMatch(it -> PICKING.equals(it.getType())); // no picking other than completed or failed
    }

    public void save(TaskBundle pickBundle) {
        taskBundleRepository.save(pickBundle);
    }

    public List<TaskBundle> findAll() {
        return taskBundleRepository.findAll();
    }

    public TaskBundle findFirstByTasksContaining(Task task) {
        return taskBundleRepository.findFirstByTasksContaining(task);
    }

    public List<TaskBundle> findNotFinished() {
        return taskBundleRepository.findAllByStatusNotIn(List.of(TaskBundleStatus.COMPLETED, TaskBundleStatus.FAILED));
    }

    public List<TaskBundle> findTaskBundles(OrderId orderId) {
        return taskBundleRepository.findAllByOrderId(orderId);
    }

    public void delete(List<TaskBundle> taskBundles) {
        taskBundleRepository.deleteAll(taskBundles);
    }
}
