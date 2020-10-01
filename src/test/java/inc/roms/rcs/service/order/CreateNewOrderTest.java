package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.NotEnoughSkuToFulfillOrderException;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.ResponseCode;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false"})
@AutoConfigureEmbeddedDatabase
public class CreateNewOrderTest extends BaseIntegrationTest {

    @Autowired
    public CreateNewOrderTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldCreateOrderIfThereIsEmptyToteAndStorage() {
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
        Optional<Order> order = orderService.getByOrderId(request.getOrderId());

        //then
        assertThat(createOrderResponse.getResponseCode()).isEqualTo(ResponseCode.ACCEPTED);
        assertThat(order).isPresent();
        assertThat(order.get().getId()).isNotNull();
    }

    @Test
    @FlywayTest
    public void shouldNotCreateOrderIfNoStockIsAvailable() {
        //given
        skus();
        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(10));

        //when
        assertThatThrownBy(() -> orderManagementService.create(request)).isInstanceOf(NotEnoughSkuToFulfillOrderException.class);
        Optional<Order> order = orderService.getByOrderId(request.getOrderId());

        //then
        assertThat(order).isEmpty();
    }

    @Test
    @FlywayTest
    public void shouldReturnProperErrorIfSkuIsUnknown() {
        //given
        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(10));

        //when
        assertThatThrownBy(() -> orderManagementService.create(request)).isInstanceOf(SkuNotFoundException.class);
    }

    @Test
    @FlywayTest
    public void shouldNotCreateOrderIfNoEmptyTotesAreAvailable() {
        //given
        skus();

        Tote storageTote = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(30).ordinal(0))
                .build();

        toteService.updateTote(storageTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(10));

        //when
        assertThatThrownBy(() -> orderManagementService.create(request)).isInstanceOf(NoEmptyTotesException.class);
        Optional<Order> order = orderService.getByOrderId(request.getOrderId());

        //then
        assertThat(order).isEmpty();
    }
}
