package inc.roms.rcs.builders;

import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;

public class PickBuilder extends TaskBuilder<Pick> {

    private ToteId sourceToteId;
    private Integer sourceSlotOrdinal;
    private ToteId destinationToteId;
    private int destinationSlotOrdinal;
    private SkuId skuId;
    private Quantity quantity;
    private Reservation reservation;

    public PickBuilder reservation(Reservation reservation) {
        this.reservation = reservation;
        return this;
    }

    public PickBuilder sourceToteId(ToteId sourceToteId) {
        this.sourceToteId = sourceToteId;
        return this;
    }

    public PickBuilder sourceSlotOrdinal(Integer sourceSlotOrdinal) {
        this.sourceSlotOrdinal = sourceSlotOrdinal;
        return this;
    }

    public PickBuilder destinationToteId(ToteId destinationToteId) {
        this.destinationToteId = destinationToteId;
        return this;
    }

    public PickBuilder destinationSlotOrdinal(int destinationSlotOrdinal) {
        this.destinationSlotOrdinal = destinationSlotOrdinal;
        return this;
    }

    public PickBuilder skuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public PickBuilder quantity(Integer quantity) {
        this.quantity = Quantity.of(quantity);
        return this;
    }

    public Pick build() {
        Pick pick = new Pick(sourceToteId, sourceSlotOrdinal, destinationToteId, destinationSlotOrdinal, skuId, quantity, null);
        pick.setStatus(this.taskStatus);
        pick.setReservation(reservation);
        pick.setFailReason(failReason);
        return pick;
    }

}
