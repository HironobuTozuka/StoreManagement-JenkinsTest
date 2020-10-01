package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderStatus;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static inc.roms.rcs.service.task.domain.model.TaskBundleType.PICKING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
class PickingTaskBundleTest extends BaseIntegrationTest {

    @Autowired
    public PickingTaskBundleTest(OrderManagementService orderManagementService,
                                 ToteService toteService,
                                 SkuService skuService,
                                 TaskBundleService taskBundleService,
                                 OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldCreatePickingTaskBundleWithOnePick() {
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
        orderManagementService.create(request);

        //then
        waitForTasksToBeProcessed(request);

        List<TaskBundle> taskBundles = taskBundleService.findAll();

        assertThat(taskBundles).hasSize(1);
        assertThat(taskBundles.get(0).getType()).isEqualTo(PICKING);
        assertThat(taskBundles.get(0).getTasks()).hasSize(1);
        assertThat(taskBundles.get(0).getTasks().get(0)).isInstanceOf(Pick.class);
    }

    private void waitForTasksToBeProcessed(CreateOrderRequest request) {
        await().atMost(Duration.of(2, ChronoUnit.SECONDS)).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && OrderStatus.NOT_STARTED.equals(order.get().getOrderStatus());
        });
    }


    @Test
    @FlywayTest
    public void shouldCreatePickingTaskBundleWithMultiplePicks() {
        //given
        skus();
        Tote storageTote1 = defaultTote()
                .toteFunction(ToteFunction.STORAGE)
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(30).ordinal(0))
                .build();

        Tote storageTote2 = defaultTote()
                .toteFunction(ToteFunction.STORAGE)
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_2).quantity(30).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote1);
        toteService.updateTote(storageTote2);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithTwoOrderLines(SKU_ID_1, Quantity.of(10), SKU_ID_2, Quantity.of(5));

        //when
        orderManagementService.create(request);

        //then
        waitForTasksToBeProcessed(request);
        List<TaskBundle> taskBundles = taskBundleService.findAll();

        assertThat(taskBundles).hasSize(1);
        assertThat(taskBundles.get(0).getType()).isEqualTo(PICKING);
        assertThat(taskBundles.get(0).getTasks()).hasSize(2);
        assertThat(taskBundles.get(0).getTasks().get(0)).isInstanceOf(Pick.class);
        assertThat(taskBundles.get(0).getTasks().get(1)).isInstanceOf(Pick.class);
    }

}