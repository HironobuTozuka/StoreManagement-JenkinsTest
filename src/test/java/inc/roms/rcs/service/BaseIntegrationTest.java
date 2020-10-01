package inc.roms.rcs.service;

import inc.roms.rcs.builders.CreateOrderRequestBuilder;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.*;
import inc.roms.rcs.vo.tote.ToteId;

import java.time.*;

import static inc.roms.rcs.builders.OrderLineModelBuilder.orderLineModel;

public abstract class BaseIntegrationTest {

    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochMilli(1000000), ZoneOffset.UTC);
    public static final LocalDateTime SELL_BY_DATE = LocalDateTime.now(FIXED_CLOCK);
    public static final LocalDate DELIVERY_DATE = LocalDate.now(FIXED_CLOCK);

    public static final ToteId STORAGE_TOTE_ID_1 = ToteId.from("SOURCE_TOTE_1");
    public static final ToteId STORAGE_TOTE_ID_2 = ToteId.from("SOURCE_TOTE_2");
    public static final ToteId DELIVERY_TOTE_ID_1 = ToteId.from("DEST_TOTE");
    public static final ToteId DELIVERY_TOTE_ID_2 = ToteId.from("DEST_TOTE_2");
    public static final ToteId DELIVERY_TOTE_ID_3 = ToteId.from("DEST_TOTE_3");
    public static final ToteId DELIVERY_TOTE_ID_4 = ToteId.from("DEST_TOTE_4");
    public static final ToteId DELIVERY_TOTE_ID_5 = ToteId.from("DEST_TOTE_5");
    public static final SkuId SKU_ID_1 = SkuId.from("SKU_1");
    public static final SkuId SKU_ID_2 = SkuId.from("SKU_2");
    public static final SkuId SKU_ID_3 = SkuId.from("SKU_3");
    public static final ExternalId SKU_EXTERNAL_ID_1 = ExternalId.from("SKU_EXTERNAL_1");
    public static final ExternalId SKU_EXTERNAL_ID_2 = ExternalId.from("SKU_EXTERNAL_2");
    public static final ExternalId SKU_EXTERNAL_ID_3 = ExternalId.from("SKU_EXTERNAL_3");
    public static final Name SKU_NAME_1 = Name.from("SKU_1_NAME");
    public static final Name SKU_NAME_2 = Name.from("SKU_2_NAME");
    public static final Name SKU_NAME_3 = Name.from("SKU_3_NAME");
    public static final OrderLineId ORDER_LINE_ID_1 = OrderLineId.from("ORDER_LINE_ID_1");
    public static final OrderId ORDER_ID = OrderId.from("ORDER");
    public static final OrderId OTHER_ORDER_ID = OrderId.from("OTHER_ORDER");
    public static final GateId GATE = GateId.from("gate-001");
    public static final UserId USER = UserId.from("USER");
    private static final OrderLineId ORDER_LINE_ID_2 = OrderLineId.from("ORDER_LINE_ID_2");
    public static final TransactionId CREATE_ORDER_TRANSACTION_ID = TransactionId.generate();
    public static final Category CATEGORY_1 = Category.from("CATEGORY_1");
    public static final Category CATEGORY_2 = Category.from("CATEGORY_1");

    protected final OrderManagementService orderManagementService;

    protected final ToteService toteService;

    protected final SkuService skuService;

    protected final TaskBundleService taskBundleService;

    protected final OrderService orderService;

    public BaseIntegrationTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService) {
        this.orderManagementService = orderManagementService;
        this.toteService = toteService;
        this.skuService = skuService;
        this.taskBundleService = taskBundleService;
        this.orderService = orderService;
    }


    protected void skus() {
        saveSku(SKU_ID_1, SKU_EXTERNAL_ID_1, CATEGORY_1, SKU_NAME_1);
        saveSku(SKU_ID_2, SKU_EXTERNAL_ID_2, CATEGORY_1, SKU_NAME_2);
        saveSku(SKU_ID_3, SKU_EXTERNAL_ID_3, CATEGORY_2, SKU_NAME_3);
    }

    private Sku saveSku(SkuId skuId, ExternalId externalSkuId, Category category1, Name skuName) {
        Sku sku = new Sku();
        sku.setSkuId(skuId);
        sku.setDimensions(new Dimensions(1, 1, 1));
        sku.setWeight(10D);
        sku.setExternalId(externalSkuId);
        sku.setCategory(category1);
        sku.setName(skuName);
        sku.setStatus(SkuStatus.READY);
        skuService.save(sku);
        return sku;
    }

    protected CreateOrderRequest createOrderWithSingleOrderLine(SkuId skuId, Quantity quantity) {
        return createOrderWithSingleOrderLine(ORDER_ID, skuId, quantity);
    }

    protected CreateOrderRequest createOrderWithSingleOrderLine(OrderId orderId, SkuId skuId, Quantity quantity) {
        return CreateOrderRequestBuilder.orderRequest()
                .orderId(orderId)
                .gateId(GATE)
                .orderType(OrderType.ORDER)
                .userId(USER)
                .transactionId(CREATE_ORDER_TRANSACTION_ID)
                .orderLines(
                        orderLineModel()
                                .orderLineId(ORDER_LINE_ID_1)
                                .quantity(quantity)
                                .skuId(skuId)
                ).build();
    }

    protected CreateOrderRequest createPreorder(OrderId orderId, SkuId skuId, Quantity quantity) {
        return CreateOrderRequestBuilder.orderRequest()
                .orderId(orderId)
                .gateId(GATE)
                .orderType(OrderType.PREORDER)
                .userId(USER)
                .transactionId(CREATE_ORDER_TRANSACTION_ID)
                .orderLines(
                        orderLineModel()
                                .orderLineId(ORDER_LINE_ID_1)
                                .quantity(quantity)
                                .skuId(skuId)
                ).build();
    }

    protected CreateOrderRequest createOrderWithTwoOrderLines(SkuId skuId1, Quantity quantity1, SkuId skuId2, Quantity quantity2) {
        return CreateOrderRequestBuilder.orderRequest()
                .orderId(ORDER_ID)
                .gateId(GATE)
                .orderType(OrderType.ORDER)
                .userId(USER)
                .orderLines(
                        orderLineModel()
                                .orderLineId(ORDER_LINE_ID_1)
                                .quantity(quantity1)
                                .skuId(skuId1),
                        orderLineModel()
                                .orderLineId(ORDER_LINE_ID_2)
                                .quantity(quantity2)
                                .skuId(skuId2)
                ).build();
    }
}
