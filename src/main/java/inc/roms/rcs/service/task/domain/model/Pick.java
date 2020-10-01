package inc.roms.rcs.service.task.domain.model;

import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.task.TaskId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@ToString(callSuper = true)
public class Pick extends Task {

    public Pick() {}

    @AttributeOverrides({@AttributeOverride(name="toteId", column=@Column(name="sourceToteId"))})
    private ToteId sourceToteId;

    private Integer sourceSlotOrdinal;

    @AttributeOverrides({@AttributeOverride(name="toteId", column=@Column(name="destinationToteId"))})
    private ToteId destinationToteId;

    private int destinationSlotOrdinal;

    private SkuId skuId;

    private Quantity quantity;

    @ManyToOne
    private Reservation reservation;

    public Pick(ToteId sourceToteId, Integer sourceSlotOrdinal, ToteId destinationToteId, Integer destinationSlotOrdinal, SkuId skuId, Quantity quantity, Reservation reservation) {
        this.sourceToteId = sourceToteId;
        this.reservation = reservation;
        this.sourceSlotOrdinal = sourceSlotOrdinal;
        this.destinationToteId = destinationToteId;
        this.destinationSlotOrdinal = destinationSlotOrdinal;
        this.skuId = skuId;
        this.quantity = quantity;
        this.setTaskId(TaskId.generate());
        this.setStatus(TaskStatus.CREATED);
    }
}
