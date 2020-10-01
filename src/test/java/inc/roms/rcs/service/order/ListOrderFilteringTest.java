package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.request.ListOrderRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.service.order.response.ListOrderResponse;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
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
public class ListOrderFilteringTest extends BaseIntegrationTest {

    @Autowired
    public ListOrderFilteringTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldFilterOrdersBySkuId() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OTHER_ORDER_ID, STORAGE_TOTE_ID_2, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().skuId(SKU_ID_1).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(ORDER_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterListByOrderId() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OTHER_ORDER_ID, STORAGE_TOTE_ID_2, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().orderId(ORDER_ID).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(ORDER_ID);
    }


    @Test
    @FlywayTest
    public void shouldFilterOutOrderByStatus() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OTHER_ORDER_ID, STORAGE_TOTE_ID_2, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().orderStatus(OrderStatus.ABANDONED).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(0);
    }

    @Test
    @FlywayTest
    public void shouldFilterOrderByStatus() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().orderStatus(OrderStatus.NOT_STARTED).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
    }

    @Test
    @FlywayTest
    public void shouldFilterOrderByStorageTote() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OTHER_ORDER_ID, STORAGE_TOTE_ID_2, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().storageToteId(STORAGE_TOTE_ID_1).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(ORDER_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterOrderByDeliveryTote() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OTHER_ORDER_ID, STORAGE_TOTE_ID_2, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder().deliveryToteId(DELIVERY_TOTE_ID_1).build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(ORDER_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterOutAnyOrderThatDoesntMatchAllConditions() {
        //given
        int orderedQuantity = 10;
        singleOrderExists(ORDER_ID, STORAGE_TOTE_ID_1, SKU_ID_1, DELIVERY_TOTE_ID_1, orderedQuantity);
        singleOrderExists(OrderId.generate(), STORAGE_TOTE_ID_1, SKU_ID_2, DELIVERY_TOTE_ID_2, orderedQuantity);
        singleOrderExists(OrderId.generate(), STORAGE_TOTE_ID_2, SKU_ID_1, DELIVERY_TOTE_ID_3, orderedQuantity);

        //when
        ListOrderRequest request = ListOrderRequest.builder()
                .storageToteId(STORAGE_TOTE_ID_1)
                .skuId(SKU_ID_1)
                .build();
        ListOrderResponse orders = orderManagementService.list(request);

        //then
        assertThat(orders.getOrders()).hasSize(1);
        assertThat(orders.getOrders().get(0).getOrderId()).isEqualTo(ORDER_ID);
    }

    private void singleOrderExists(OrderId orderId, ToteId sourceToteId, SkuId skuId, ToteId destinationToteId, int quantity) {
        skus();
        Tote storageTote = defaultTote()
                .toteId(sourceToteId)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(skuId).quantity(quantity).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(destinationToteId)
                .build();

        toteService.updateTote(storageTote);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(orderId, skuId, Quantity.of(quantity));

        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        Awaitility.await().until(() -> createOrderResponse.getResponseDetails().getPickOrderFuture().isDone());
    }
}
