package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
class DeliverToteReservationTest extends BaseIntegrationTest {

    @Autowired
    public DeliverToteReservationTest(OrderManagementService orderManagementService,
                                      ToteService toteService,
                                      SkuService skuService,
                                      TaskBundleService taskBundleService,
                                      OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldReserveToteForDelivery() {
        //given
        skus();
        Tote storageTote = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(30).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(10));

        //when
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        //then
        Tote deliveryTote = toteService.getToteById(destinationTote.getId());
        assertThat(createOrderResponse.getResponseCode()).isEqualTo(ResponseCode.ACCEPTED);
        assertThat(getDeliverySlotByOrdinal(deliveryTote, 0).getOrderId()).isEqualTo(request.getOrderId());
    }

    private DeliveryInventory getDeliverySlotByOrdinal(Tote storageToteAfterOrder, int ordinal) {
        return storageToteAfterOrder.getSlots().stream().filter(it -> it.getOrdinal() == ordinal).findAny().get().getDeliveryInventory();
    }
}