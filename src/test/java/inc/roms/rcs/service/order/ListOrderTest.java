package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.request.ListOrderRequest;
import inc.roms.rcs.service.order.response.*;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.awaitility.Awaitility;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
public class ListOrderTest extends BaseIntegrationTest {

    @Autowired
    public ListOrderTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldIncludeOrderBasicData() {
        //given
        int orderedQuantity = 10;
        CreateOrderRequest request = singleOrderExists(STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);

        //when
        ListOrderResponse orders = orderManagementService.list(ListOrderRequest.builder().build());

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(request.getOrderId());
        assertThat(orders.getOrders().get(0).getGate()).isEqualTo(request.getGateId());
        assertThat(orders.getOrders().get(0).getUserId()).isEqualTo(request.getUserId());
        assertThat(orders.getOrders().get(0).getPickupTime()).isEqualTo(request.getPickupTime());
        assertThat(orders.getOrders().get(0).getOrderType()).isEqualTo(request.getOrderType());
        assertThat(orders.getOrders().get(0).getOrderStatus()).isEqualTo(OrderStatus.NOT_STARTED);
    }

    @Test
    @FlywayTest
    public void shouldIncludeOrderLines() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);

        //when
        ListOrderResponse orders = orderManagementService.list(ListOrderRequest.builder().build());

        //then
        assertThat(orders.getOrders().get(0).getOrderLines()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderLines().get(0).getSkuId()).isEqualTo(SKU_ID_1);
        assertThat(orders.getOrders().get(0).getOrderLines().get(0).getQuantity()).isEqualTo(Quantity.of(orderedQuantity));
    }


    @Test
    @FlywayTest
    public void shouldIncludeTotes() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);

        //when
        ListOrderResponse orders = orderManagementService.list(ListOrderRequest.builder().build());

        //then
        assertThat(orders.getOrders().get(0).getDeliveryTotes()).containsExactly(DELIVERY_TOTE_ID_1);
        assertThat(orders.getOrders().get(0).getOrderLines().get(0).getStorageTotes()).containsExactly(STORAGE_TOTE_ID_1);
    }

    private CreateOrderRequest singleOrderExists(ToteId sourceToteId, SkuId skuId, ToteId destinationToteId, int quantity) {
        skus();
        Tote storageTote = defaultTote()
                .toteId(sourceToteId)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(skuId).quantity(30).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteId(destinationToteId)
                .toteFunction(ToteFunction.DELIVERY)
                .build();

        toteService.updateTote(storageTote);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(skuId, Quantity.of(quantity));

        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        Awaitility.await().until(() -> createOrderResponse.getResponseDetails().getPickOrderFuture().isDone());

        return request;
    }
}
