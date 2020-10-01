package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.builders.ReservationBuilder;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.TaskDetails;
import inc.roms.rcs.service.task.domain.model.TaskStatus;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static inc.roms.rcs.builders.DeliverySlotBuilder.deliverySlot;
import static inc.roms.rcs.builders.OrderBuilder.order;
import static inc.roms.rcs.builders.OrderLineBuilder.orderLine;
import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.TaskBuilder.pick;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PickUpdateListenerTest {

    public static final ToteId SOURCE_TOTE_ID = ToteId.from("TOTE_1");
    public static final SkuId SKU_ID = SkuId.from("SKU_1");
    private static final ToteId DESTINATION_TOTE_ID = ToteId.from("DEST_TOTE");
    public static final int REQUESTED_QUANTITY = 5;
    public static final int TOTAL_QUANTITY = 10;
    public static final int QUANTITY_LEFT = TOTAL_QUANTITY - REQUESTED_QUANTITY;
    public static final int RESERVATION_ID = 0;

    private final ToteService toteService = mock(ToteService.class);
    private final OrderService orderService = mock(OrderService.class);
    private final ReservationService reservationService = mock(ReservationService.class);
    private final OmniChannelService omniChannelService = mock(OmniChannelService.class);

    private final PickUpdateListener pickUpdateListener = new PickUpdateListener(toteService, orderService, reservationService, omniChannelService);


    @Test
    public void shouldThrowIllegalStateExceptionIfDeliveryInventoryDoesNotExist() {
        Reservation reservation = reservation();
        Tote sourceTote = sourceTote(reservation);
        incorrectDeliveryTote();

        Pick pick = pick()
                .destinationToteId(DESTINATION_TOTE_ID)
                .destinationSlotOrdinal(0)
                .sourceToteId(SOURCE_TOTE_ID)
                .sourceSlotOrdinal(0)
                .skuId(SKU_ID)
                .quantity(5)
                .reservation(sourceTote.getSlotByOrdinal(0).getStorageInventory().getReservations().get(0))
                .build();

        TaskDetails details = new TaskDetails();
        details.setFailed(Quantity.of(0));
        details.setPicked(Quantity.of(5));
        TaskUpdateRequest updateRequest = new TaskUpdateRequest(pick.getTaskId().getTaskId(), TaskStatus.COMPLETED, details);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> pickUpdateListener.onTaskUpdate(pick, updateRequest));
    }

    @Test
    public void shouldModifyInventoryOnSuccessfulPick() {
        Reservation reservation = reservation();
        Tote sourceTote = sourceTote(reservation);
        Tote destinationTote = deliveryTote();

        Pick pick = pick()
                .destinationToteId(DESTINATION_TOTE_ID)
                .destinationSlotOrdinal(0)
                .sourceToteId(SOURCE_TOTE_ID)
                .sourceSlotOrdinal(0)
                .skuId(SKU_ID)
                .quantity(REQUESTED_QUANTITY)
                .reservation(reservation)
                .build();

        TaskDetails details = new TaskDetails();
        details.setFailed(Quantity.of(0));
        details.setPicked(Quantity.of(5));
        TaskUpdateRequest updateRequest = new TaskUpdateRequest(pick.getTaskId().getTaskId(), TaskStatus.COMPLETED, details);

        pickUpdateListener.onTaskUpdate(pick, updateRequest);

        assertThat(destinationTote.getSlotByOrdinal(0).getDeliveryInventory().getSkuBatches()).hasSize(1);
        assertThat(destinationTote.getSlotByOrdinal(0).getDeliveryInventory().getSkuBatches().get(SKU_ID).getQuantity()).isEqualTo(Quantity.of(REQUESTED_QUANTITY));
        assertThat(sourceTote.getSlotByOrdinal(0).getStorageInventory().getSkuBatch().getQuantity()).isEqualTo(Quantity.of(QUANTITY_LEFT));
    }

    @Test
    public void shouldNotModifyInventoryOnFailedPick() {
        Reservation reservation = reservation();
        Tote sourceTote = sourceTote(reservation);
        Tote destinationTote = deliveryTote();

        Pick pick = pick()
                .destinationToteId(DESTINATION_TOTE_ID)
                .destinationSlotOrdinal(0)
                .sourceToteId(SOURCE_TOTE_ID)
                .sourceSlotOrdinal(0)
                .skuId(SKU_ID)
                .quantity(REQUESTED_QUANTITY)
                .reservation(reservation)
                .build();

        TaskDetails details = new TaskDetails();
        details.setFailed(Quantity.of(5));
        details.setPicked(Quantity.of(0));
        TaskUpdateRequest updateRequest = new TaskUpdateRequest(pick.getTaskId().getTaskId(), TaskStatus.FAILED, details);

        pickUpdateListener.onTaskUpdate(pick, updateRequest);

        assertThat(destinationTote.getSlotByOrdinal(0).getDeliveryInventory().getSkuBatches()).hasSize(0);
        assertThat(sourceTote.getSlotByOrdinal(0).getStorageInventory().getSkuBatch().getQuantity()).isEqualTo(Quantity.of(TOTAL_QUANTITY));
    }

    private Reservation reservation() {
        Reservation reservation = ReservationBuilder
                .reservation().id(RESERVATION_ID).orderLineBuilder(orderLine().order(order().orderType(OrderType.ORDER))).build();
        when(reservationService.getById(eq(RESERVATION_ID))).thenReturn(reservation);
        return reservation;
    }

    @Test
    public void shouldRemoveReservation() {
        Reservation reservation = reservation();
        Tote sourceTote = sourceTote(reservation);
        deliveryTote();

        Pick pick = pick()
                .destinationToteId(DESTINATION_TOTE_ID)
                .destinationSlotOrdinal(0)
                .sourceToteId(SOURCE_TOTE_ID)
                .sourceSlotOrdinal(0)
                .skuId(SKU_ID)
                .quantity(REQUESTED_QUANTITY)
                .reservation(sourceTote.getSlotByOrdinal(0).getStorageInventory().getReservations().get(0))
                .build();

        TaskDetails details = new TaskDetails();
        details.setFailed(Quantity.of(0));
        details.setPicked(Quantity.of(5));
        TaskUpdateRequest updateRequest = new TaskUpdateRequest(pick.getTaskId().getTaskId(), TaskStatus.COMPLETED, details);

        pickUpdateListener.onTaskUpdate(pick, updateRequest);

        assertThat(sourceTote.getSlotByOrdinal(0).getStorageInventory().getReservations()).hasSize(0);
    }

    private Tote deliveryTote() {
        Tote destinationTote = defaultTote()
                .toteId(DESTINATION_TOTE_ID)
                .slots(deliverySlot().ordinal(0).orderId(OrderId.generate()))
                .build();

        when(toteService.findToteByToteId(eq(DESTINATION_TOTE_ID))).thenReturn(Optional.of(destinationTote));

        return destinationTote;
    }

    private void incorrectDeliveryTote() {
        Tote destinationTote = defaultTote()
                .toteId(DESTINATION_TOTE_ID)
                .build();

        when(toteService.findToteByToteId(eq(DESTINATION_TOTE_ID))).thenReturn(Optional.of(destinationTote));
    }

    private Tote sourceTote(Reservation reservation) {
        Tote sourceTote = defaultTote()
                .toteId(SOURCE_TOTE_ID)
                .slots(storageSlot()
                        .ordinal(0)
                        .skuId(SKU_ID)
                        .quantity(TOTAL_QUANTITY)
                        .reservations(reservation))
                .build();

        when(toteService.findToteByToteId(eq(SOURCE_TOTE_ID))).thenReturn(Optional.of(sourceTote));

        return sourceTote;
    }
}