package inc.roms.rcs.service.task.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.task.TaskBundleId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
public class TaskBundle {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TaskBundleStatus status;

    @Enumerated(EnumType.STRING)
    private TaskBundleType type;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastTaskUpdate;

    private OrderId orderId;

    private TaskBundleId taskBundleId;

    public TaskBundle add(List<? extends Task> tasks) {
        this.tasks.addAll(tasks);
        return this;
    }

    public TaskBundle add(Task task) {
        this.tasks.add(task);
        return this;
    }

    public boolean updateStatus() {
        if(allTasksHaveStatus(TaskStatus.CREATED)) {
            return this.status(TaskBundleStatus.CREATED);
        } else if(anyTaskHasStatus(TaskStatus.CANCELLED) && noTaskHasStatus(TaskStatus.CREATED)) {
            return this.status(TaskBundleStatus.CANCELLED);
        } else if(anyTaskHasStatus(TaskStatus.FAILED) && noTaskHasStatus(TaskStatus.CREATED)) {
            return this.status(TaskBundleStatus.FAILED);
        } else if(anyTaskHasStatus(TaskStatus.FAILED)) {
            return this.status(TaskBundleStatus.FAILING);
        } else if(allTasksHaveStatus(TaskStatus.COMPLETED)) {
            return this.status(TaskBundleStatus.COMPLETED);
        } else if(anyTaskHasStatus(TaskStatus.COMPLETED)) {
            return this.status(TaskBundleStatus.IN_PROGRESS);
        }

        return false;
    }

    public boolean status(TaskBundleStatus status) {
        if(this.status.equals(status)) return false;
        this.status = status;
        return true;
    }

    private boolean anyTaskHasStatus(TaskStatus failed) {
        return getTasks().stream().anyMatch(it -> it.getStatus().equals(failed));
    }

    private boolean allTasksHaveStatus(TaskStatus status) {
        return getTasks().stream().allMatch(it -> it.getStatus().equals(status));
    }

    private boolean noTaskHasStatus(TaskStatus created) {
        return getTasks().stream().noneMatch(it -> it.getStatus().equals(created));
    }

    public static TaskBundle moveBundle() {
        TaskBundle taskBundle = new TaskBundle();
        taskBundle.setTaskBundleId(TaskBundleId.generate());
        taskBundle.setType(TaskBundleType.MOVE);
        taskBundle.setStatus(TaskBundleStatus.CREATED);
        return taskBundle;
    }

    public static TaskBundle deliveryBundle() {
        TaskBundle taskBundle = new TaskBundle();
        taskBundle.setTaskBundleId(TaskBundleId.generate());
        taskBundle.setType(TaskBundleType.DELIVERY);
        taskBundle.setStatus(TaskBundleStatus.CREATED);
        return taskBundle;
    }

    public static TaskBundle deliveryBundle(OrderId orderId) {
        TaskBundle taskBundle = new TaskBundle();
        taskBundle.setTaskBundleId(TaskBundleId.generate());
        taskBundle.setOrderId(orderId);
        taskBundle.setType(TaskBundleType.DELIVERY);
        taskBundle.setStatus(TaskBundleStatus.CREATED);
        return taskBundle;
    }

    public static TaskBundle pickBundle(OrderId orderId) {
        TaskBundle taskBundle = new TaskBundle();
        taskBundle.setTaskBundleId(TaskBundleId.generate());
        taskBundle.setOrderId(orderId);
        taskBundle.setType(TaskBundleType.PICKING);
        taskBundle.setStatus(TaskBundleStatus.CREATED);
        return taskBundle;
    }

    @Transient
    public Optional<FailReason> getFailReason() {
        return tasks.stream().filter(it -> it.getStatus().equals(TaskStatus.FAILED))
                .filter(it -> it.getFailReason() != null)
                .map(Task::getFailReason).findFirst();
    }
}
