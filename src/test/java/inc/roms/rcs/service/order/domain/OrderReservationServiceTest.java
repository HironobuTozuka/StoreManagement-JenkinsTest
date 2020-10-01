package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.tote.ToteStatus;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.awaitility.Awaitility;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.deliveryTote;
import static inc.roms.rcs.builders.ToteBuilder.storageTote;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false"})
@AutoConfigureEmbeddedDatabase
class OrderReservationServiceTest extends BaseIntegrationTest {

    private final OrderReservationService orderReservationService;

    @Autowired
    public OrderReservationServiceTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, OrderReservationService orderReservationService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.orderReservationService = orderReservationService;
    }

    @Test
    @FlywayTest
    public void shouldMakeDeliveryToteReservationsOnDeliveryToteInError() {
        //given
        skus();
        Tote st1 = storageTote()
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(30).ordinal(0))
                .build();

        Tote st2 = storageTote()
                .toteId(STORAGE_TOTE_ID_2)
                .slots(storageSlot().skuId(SKU_ID_2).quantity(30).ordinal(0))
                .build();

        Tote dt1 = deliveryTote()
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(dt1);
        toteService.updateTote(st1);
        toteService.updateTote(st2);

        CreateOrderRequest orderRequest = createOrderWithTwoOrderLines(SKU_ID_1, Quantity.of(1), SKU_ID_2, Quantity.of(1));
        CreateOrderResponse createOrderResponse = orderManagementService.create(orderRequest);

        Awaitility.await().atMost(Duration.of(30, ChronoUnit.SECONDS))
                .until(() -> createOrderResponse.getResponseDetails().getPickOrderFuture().isDone());

        Tote dt2 = deliveryTote()
                .toteId(DELIVERY_TOTE_ID_2)
                .build();
        toteService.updateTote(dt2);
        toteService.markAsFailing(dt1.getToteId(), ToteStatus.IN_ERROR);

        Order order = orderService.getByOrderId(orderRequest.getOrderId()).orElseThrow();

        //when
        orderReservationService.reserveDeliverySlots(order);

        //then
        Optional<Tote> dt2AfterUpdate = toteService.findToteByToteId(dt2.getToteId());

        assertThat(dt2AfterUpdate).isPresent();

        Tote actualDt2 = dt2AfterUpdate.orElseThrow();
        List<DeliveryInventory> dt2DeliveryInventory = actualDt2.getSlots()
                .stream()
                .filter(it -> it.getDeliveryInventory() != null)
                .map(Slot::getDeliveryInventory)
                .collect(toList());

        List<Reservation> reservations = orderReservationService.getReservationsFor(order);

        assertThat(dt2DeliveryInventory).hasSize(1);
        assertThat(dt2DeliveryInventory.get(0).getOrderId()).isEqualTo(orderRequest.getOrderId());
        assertThat(reservations.get(0).getDeliveryInventory()).isEqualTo(dt2DeliveryInventory.get(0));
    }

}