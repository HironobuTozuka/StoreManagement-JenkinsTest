package inc.roms.rcs.service.order;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.inventory.request.ToteListRequest;
import inc.roms.rcs.service.inventory.response.ToteListResponse;
import inc.roms.rcs.service.omnichannel.kannart.OmniChannelECClient;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.request.DeliverOrderRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.tools.MheOperatorMockController;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.tote.ToteStatus;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.awaitility.Awaitility;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static inc.roms.rcs.builders.StorageSlotBuilder.storageSlot;
import static inc.roms.rcs.builders.ToteBuilder.defaultTote;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(name = "mockedOmnichannel")
@SpringBootTest(properties = {"spring.profiles.active=e2e", "zonky.test.database.postgres.client.properties.currentSchema=sm"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT )
@AutoConfigureEmbeddedDatabase
public class E2EOrderTest extends BaseIntegrationTest {

    Logger log = LoggerFactory.getLogger(E2EOrderTest.class);

    @Autowired
    private MheOperatorMockController mheOperatorMockController;

    @MockBean
    private OmniChannelECClient omniChannelECClient;

    @SpyBean
    private FeatureFlagService featureFlagService;

    public static final Tote STORAGE_TOTE = defaultTote()
            .toteId(STORAGE_TOTE_ID_1)
            .toteFunction(ToteFunction.STORAGE)
            .slots(storageSlot().skuId(SKU_ID_1).quantity(30).ordinal(0))
            .build();

    public static final Tote DESTINATION_TOTE = defaultTote()
            .toteFunction(ToteFunction.DELIVERY)
            .toteId(DELIVERY_TOTE_ID_1)
            .build();

    public static final Tote DESTINATION_TOTE_2 = defaultTote()
            .toteId(DELIVERY_TOTE_ID_2)
            .toteFunction(ToteFunction.DELIVERY)
            .build();

    public static final Tote DESTINATION_TOTE_3 = defaultTote()
            .toteId(DELIVERY_TOTE_ID_3)
            .toteFunction(ToteFunction.DELIVERY)
            .build();

    public static final Tote DESTINATION_TOTE_4 = defaultTote()
            .toteId(DELIVERY_TOTE_ID_4)
            .toteFunction(ToteFunction.DELIVERY)
            .build();

    public static final Tote DESTINATION_TOTE_5 = defaultTote()
            .toteId(DELIVERY_TOTE_ID_5)
            .toteFunction(ToteFunction.DELIVERY)
            .build();

    @Autowired
    public E2EOrderTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
    }

    @Test
    @FlywayTest
    public void shouldCollectOrder() {
        Mockito.when(featureFlagService.isDelayedPickPreorder()).thenCallRealMethod();

        skus();
        totes();

        OrderId orderId = OrderId.generate();

        CreateOrderRequest request = createOrderWithSingleOrderLine(orderId, SKU_ID_1, Quantity.of(10));

        //when
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        assertThat(createOrderResponse.getResponseDetails().getPickOrderFuture()).isNotNull();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && order.get().getOrderStatus().equals(OrderStatus.COLLECTED);
        });
    }

    private void addToteIfNotExists(Tote storageTote) {
        Optional<Tote> toteByToteId = toteService.findToteByToteId(storageTote.getToteId());
        if(toteByToteId.isPresent()) {
            log.info("Tote {} already exists in db, not saving!", storageTote.getToteId());
            return;
        }
        toteService.updateTote(storageTote);
    }


    @Test
    @FlywayTest
    public void shouldPreparePreorder() {
        delayedPickPreorderSwitchedOn();
        skus();
        totes();

        OrderId orderId = OrderId.generate();

        CreateOrderRequest request = createPreorder(orderId, SKU_ID_1, Quantity.of(10));

        //when
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        //then
        assertThat(createOrderResponse.getResponseDetails().getPickOrderFuture()).isNull();

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && order.get().getOrderStatus().equals(OrderStatus.READY_TO_BE_PICKED);
        });

        DeliverOrderRequest deliverRequest = new DeliverOrderRequest();
        deliverRequest.setGateId(GATE);
        deliverRequest.setOrderId(orderId);
        deliverRequest.setTransactionId(TransactionId.generate());
        orderManagementService.deliver(deliverRequest);

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && order.get().getOrderStatus().equals(OrderStatus.COLLECTED);
        });
    }

    private void delayedPickPreorderSwitchedOn() {
        Mockito.when(featureFlagService.isDelayedPickPreorder()).thenReturn(true);
    }

    private void totes() {
        addToteIfNotExists(STORAGE_TOTE);
        addToteIfNotExists(DESTINATION_TOTE);
        addToteIfNotExists(DESTINATION_TOTE_2);
        addToteIfNotExists(DESTINATION_TOTE_3);
        addToteIfNotExists(DESTINATION_TOTE_4);
        addToteIfNotExists(DESTINATION_TOTE_5);
    }


    @Test
    @FlywayTest
    public void shouldPickNotDelayedPreorder() {
        Mockito.when(featureFlagService.isDelayedPickPreorder()).thenCallRealMethod();

        skus();
        totes();

        OrderId orderId = OrderId.generate();

        CreateOrderRequest request = createPreorder(orderId, SKU_ID_1, Quantity.of(10));

        //when
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        assertThat(createOrderResponse.getResponseDetails().getPickOrderFuture()).isNotNull();

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && order.get().getOrderStatus().equals(OrderStatus.PREORDER_READY);
        });
    }

    @Test
    @FlywayTest
    public void shouldCollectOrderEvenIfOneToteIsFailing() {
        Mockito.when(featureFlagService.isDelayedPickPreorder()).thenCallRealMethod();

        skus();
        totes();

        OrderId orderId = OrderId.generate();

        CreateOrderRequest request = createOrderWithSingleOrderLine(orderId, SKU_ID_1, Quantity.of(10));

        mheOperatorMockController.failNextPick();

        //when
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);

        assertThat(createOrderResponse.getResponseDetails().getPickOrderFuture()).isNotNull();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> {
            Optional<Order> order = orderService.getByOrderId(request.getOrderId());
            return order.isPresent() && order.get().getOrderStatus().equals(OrderStatus.COLLECTED);
        });
    }
}
