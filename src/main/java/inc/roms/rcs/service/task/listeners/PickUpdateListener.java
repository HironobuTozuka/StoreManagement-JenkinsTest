package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static inc.roms.rcs.service.task.domain.model.FailReason.DEST_TOTE_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickUpdateListener extends TaskUpdateListener<Pick> {

    private final ToteService toteService;
    private final OrderService orderService;
    private final ReservationService reservationService;
    private final OmniChannelService omniChannelService;

    @Override
    protected Class<Pick> classOfInterest() {
        return Pick.class;
    }

    @Override
    protected void onTaskUpdate(Pick task, TaskUpdateRequest taskUpdateRequest) {
        Tote sourceTote = toteService.findToteByToteId(task.getSourceToteId()).orElseThrow();
        Tote destinationTote = toteService.findToteByToteId(task.getDestinationToteId()).orElseThrow();

        Slot sourceSlot = sourceTote.getSlotByOrdinal(task.getSourceSlotOrdinal());
        Slot destinationSlot = destinationTote.getSlotByOrdinal(task.getDestinationSlotOrdinal());

        if (destinationSlot.getDeliveryInventory() == null && taskUpdateRequest.getDetails().getPicked().gt(0)) {
            throw new IllegalStateException();
        }

        Reservation reservation = reservationService.getById(task.getReservation().getId());

        updateOrderLine(reservation, taskUpdateRequest);

        if (taskUpdateRequest.getDetails().getPicked().gt(0) && destinationSlot.getDeliveryInventory() != null) {
            SkuBatch deliverySkuBatch = destinationSlot.getDeliveryInventory().getOrCreateSkuBatch(task.getSkuId());
            deliverySkuBatch.add(taskUpdateRequest.getDetails().getPicked());
        }

        sourceSlot.getStorageInventory().getSkuBatch().subtract(taskUpdateRequest.getDetails().getPicked());

        if(taskUpdateRequest.getDetails() == null || !DEST_TOTE_ERROR.equals(taskUpdateRequest.getDetails().getFailReason())) {
            log.debug("{}: Removing reservation!", taskUpdateRequest.getTaskId());
            sourceSlot.getStorageInventory().getReservations().remove(task.getReservation());
        } else {
            log.debug("{}: not removing reservation!", taskUpdateRequest.getTaskId());
        }

        sourceSlot.getStorageInventory().recalculate();

        omniChannelService.updateInventory(
                sourceSlot.getStorageInventory().getSkuBatch(),
                taskUpdateRequest,
                reservation.getOrderLine().getOrder().getOrderType()
        );
    }

    private void updateOrderLine(Reservation reservation, TaskUpdateRequest taskUpdateRequest) {
        OrderLine orderLine = reservation.getOrderLine();
        orderLine.addFailed(taskUpdateRequest.getDetails().getFailed());
        orderLine.addPicked(taskUpdateRequest.getDetails().getPicked());
        orderService.save(orderLine);
    }

}
