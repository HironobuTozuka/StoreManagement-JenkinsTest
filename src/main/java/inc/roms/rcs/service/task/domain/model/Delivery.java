package inc.roms.rcs.service.task.domain.model;

import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.task.TaskId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
public class Delivery extends Task {
    private ToteId toteId;
    private OrderId orderId;

    public Delivery(ToteId toteId, OrderId orderId) {
        this.toteId = toteId;
        this.orderId = orderId;
        this.setTaskId(TaskId.generate());
        this.setStatus(TaskStatus.CREATED);
    }

    public Delivery(ToteId toteId) {
        this.toteId = toteId;
        this.setTaskId(TaskId.generate());
        this.setStatus(TaskStatus.CREATED);
    }
}
