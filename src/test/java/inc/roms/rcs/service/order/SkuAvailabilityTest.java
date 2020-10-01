package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.StorageInventory;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.NotEnoughSkuToFulfillOrderException;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.response.Reason;
import inc.roms.rcs.service.order.response.RejectedSku;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.assertj.core.api.ThrowableAssert;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false"})
@AutoConfigureEmbeddedDatabase
class SkuAvailabilityTest extends BaseIntegrationTest {

    @Autowired
    public SkuAvailabilityTest(OrderManagementService orderManagementService,
                               ToteService toteService,
                               SkuService skuService,
                               TaskBundleService taskBundleService,
                               OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldReduceAvailableSkuInSingleSlot() {
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
        Tote storageToteAfterOrder = toteService.getToteById(storageTote.getId());
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 0).getAvailable()).isEqualTo(Quantity.of(20));
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 0).getQuantity()).isEqualTo(Quantity.of(30));
    }


    @Test
    @FlywayTest
    public void shouldReduceAvailableSkuInMultipleSlots() {
        //given
        skus();
        Tote storageTote = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(0),
                        storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(1))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(15));

        //when
        orderManagementService.create(request);

        //then
        Tote storageToteAfterOrder = toteService.getToteById(storageTote.getId());
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 0).getAvailable()).isEqualTo(Quantity.of(0));
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 0).getQuantity()).isEqualTo(Quantity.of(10));
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 1).getAvailable()).isEqualTo(Quantity.of(5));
        assertThat(getSlotByOrdinal(storageToteAfterOrder, 1).getQuantity()).isEqualTo(Quantity.of(10));
    }

    @Test
    @FlywayTest
    public void shouldReduceAvailableSkuInMultipleTotes() {
        //given
        skus();
        Tote storageTote1 = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(0))
                .build();

        Tote storageTote2 = defaultTote()
                .toteFunction(ToteFunction.STORAGE)
                .toteId(STORAGE_TOTE_ID_2)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote1);
        toteService.updateTote(storageTote2);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(15));

        orderManagementService.create(request);

        Tote storageTote1AfterOrder = toteService.getToteById(storageTote1.getId());
        Tote storageTote2AfterOrder = toteService.getToteById(storageTote2.getId());

        assertThat(getSlotByOrdinal(storageTote1AfterOrder, 0).getAvailable()).isEqualTo(Quantity.of(0));
        assertThat(getSlotByOrdinal(storageTote1AfterOrder, 0).getQuantity()).isEqualTo(Quantity.of(10));
        assertThat(getSlotByOrdinal(storageTote2AfterOrder, 0).getAvailable()).isEqualTo(Quantity.of(5));
        assertThat(getSlotByOrdinal(storageTote2AfterOrder, 0).getQuantity()).isEqualTo(Quantity.of(10));
    }

    @Test
    @FlywayTest
    public void shouldntReduceAvailableQuantityIfThereIsNotEnoughSkuToFulFillOrder() {
        //given
        skus();
        Tote storageTote1 = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(1).ordinal(0))
                .build();
        Tote storageTote2 = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(1).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote1);
        toteService.updateTote(storageTote2);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(4));

        //when
        assertThatThrownBy(() -> orderManagementService.create(request)).isInstanceOf(NotEnoughSkuToFulfillOrderException.class);

        Tote storageTote1AfterException = toteService.getToteById(storageTote1.getId());
        Tote storageTote2AfterException = toteService.getToteById(storageTote2.getId());

        assertThat(getSlotByOrdinal(storageTote1AfterException, 0).getAvailable()).isEqualTo(Quantity.of(1));
        assertThat(getSlotByOrdinal(storageTote2AfterException, 0).getAvailable()).isEqualTo(Quantity.of(1));
    }

    @Test
    @FlywayTest
    public void shouldThrowExceptionWithListOfRejectedSkusIfNotEnoughInventoryInRCS() {
        //given
        skus();
        Tote storageTote = defaultTote()
                .toteId(STORAGE_TOTE_ID_1)
                .toteFunction(ToteFunction.STORAGE)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(3).ordinal(0))
                .build();

        Tote destinationTote = defaultTote()
                .toteFunction(ToteFunction.DELIVERY)
                .toteId(DELIVERY_TOTE_ID_1)
                .build();

        toteService.updateTote(storageTote);
        toteService.updateTote(destinationTote);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(4));

        //when
        ThrowableAssert.ThrowingCallable createOrderCallable = () -> orderManagementService.create(request);


        //then
        assertThatThrownBy(createOrderCallable)
                .isInstanceOf(NotEnoughSkuToFulfillOrderException.class)
                .hasFieldOrPropertyWithValue("rejectedSkus", List.of(new RejectedSku(SKU_ID_1, Reason.NOT_ENOUGH, Quantity.of(1))));

    }


    @Test
    @FlywayTest
    public void shouldntReduceAvailableQuantityIfThereIsNoEmptyTote() {
        //given
        skus();
        Tote storageTote1 = defaultTote()
                .toteFunction(ToteFunction.STORAGE)
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(0))
                .build();
        Tote storageTote2 = defaultTote()
                .toteFunction(ToteFunction.STORAGE)
                .toteId(STORAGE_TOTE_ID_1)
                .slots(storageSlot().skuId(SKU_ID_1).quantity(10).ordinal(0))
                .build();

        toteService.updateTote(storageTote1);
        toteService.updateTote(storageTote2);

        CreateOrderRequest request = createOrderWithSingleOrderLine(SKU_ID_1, Quantity.of(5));

        //when
        assertThatThrownBy(() -> orderManagementService.create(request)).isInstanceOf(NoEmptyTotesException.class);

        //then
        Tote storageTote1AfterException = toteService.getToteById(storageTote1.getId());
        Tote storageTote2AfterException = toteService.getToteById(storageTote2.getId());

        assertThat(getSlotByOrdinal(storageTote1AfterException, 0).getAvailable()).isEqualTo(Quantity.of(10));
        assertThat(getSlotByOrdinal(storageTote2AfterException, 0).getAvailable()).isEqualTo(Quantity.of(10));
    }

    private StorageInventory getSlotByOrdinal(Tote storageToteAfterOrder, int ordinal) {
        return storageToteAfterOrder.getSlots().stream().filter(it -> it.getOrdinal() == ordinal).findAny().get().getStorageInventory();
    }
}