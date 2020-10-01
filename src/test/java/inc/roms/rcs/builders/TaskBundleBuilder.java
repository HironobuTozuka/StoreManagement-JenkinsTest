package inc.roms.rcs.builders;

import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.service.task.domain.model.TaskBundleStatus;
import inc.roms.rcs.service.task.domain.model.TaskBundleType;
import inc.roms.rcs.vo.order.OrderId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TaskBundleBuilder {

    private List<TaskBuilder<Task>> tasks = new ArrayList<>();
    private TaskBundleStatus status;
    private OrderId orderId;
    private TaskBundleType type;

    public static TaskBundleBuilder taskBundle() {
        return new TaskBundleBuilder();
    }

    public TaskBundleBuilder tasks(TaskBuilder... tasks) {
        this.tasks = Arrays.asList(tasks);
        return this;
    }

    public TaskBundleBuilder status(TaskBundleStatus status) {
        this.status = status;
        return this;
    }

    public TaskBundleBuilder orderId(OrderId orderId) {
        this.orderId = orderId;
        return this;
    }

    public TaskBundle build() {
        TaskBundle taskBundle = new TaskBundle();
        taskBundle.setStatus(status);
        taskBundle.setOrderId(orderId);
        taskBundle.setType(type);
        taskBundle.setTasks(tasks.stream().map(TaskBuilder::build).collect(toList()));
        return taskBundle;
    }

    public TaskBundleBuilder type(TaskBundleType type) {
        this.type = type;
        return this;
    }
}
