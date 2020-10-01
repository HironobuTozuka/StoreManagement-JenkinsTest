package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.configuration.ConfigKey;
import inc.roms.rcs.service.configuration.ConfigurationService;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.order.config.GateProperties;
import inc.roms.rcs.service.order.domain.repository.OrderLineRepository;
import inc.roms.rcs.service.order.domain.repository.OrderRepository;
import inc.roms.rcs.service.order.domain.repository.OrderTransactionRepository;
import inc.roms.rcs.service.order.exception.UnknownGateException;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.zones.ZoneId;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Optional;

import static inc.roms.rcs.builders.CreateOrderRequestBuilder.orderRequest;
import static inc.roms.rcs.builders.OrderLineModelBuilder.orderLineModel;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    public static final GateId GATE_ID = GateId.from("GATE_ID");
    public static final GateId WRONG_GATE = GateId.from("WRONG_GATE");
    public static final SkuId SKU_ID = SkuId.from("SKU_ID");

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderTransactionRepository orderTransactionRepository = mock(OrderTransactionRepository.class);
    private final OrderLineRepository orderLineRepository = mock(OrderLineRepository.class);
    private final SkuService skuService = mock(SkuService.class);
    private final TaskBundleService taskBundleService = mock(TaskBundleService.class);
    private final ReservationService reservationService = mock(ReservationService.class);
    private final Clock clock = mock(Clock.class);
    private final BusinessExceptions businessExceptions = mock(BusinessExceptions.class);
    private final ConfigurationService configurationService = mock(ConfigurationService.class);
    private final FeatureFlagService featureFlagService = mock(FeatureFlagService.class);
    private final GateProperties gateProperties = mock(GateProperties.class);

    private final OrderService orderService = new OrderService(
            orderRepository,
            orderTransactionRepository,
            orderLineRepository,
            skuService,
            taskBundleService,
            reservationService,
            clock,
            businessExceptions,
            configurationService,
            featureFlagService,
            gateProperties
    );

    @Test
    public void shouldValidateGate() {
        when(gateProperties.gateZone(GATE_ID)).thenReturn(Optional.of(ZoneId.from("ORDER_GATE")));
        when(configurationService.getConfigValue(ConfigKey.MAX_ORDER_SIZE)).thenReturn(200);
        when(skuService.getReadySku(SKU_ID)).thenReturn(new Sku());

        CreateOrderRequest request = orderRequest()
                .gateId(GATE_ID)
                .orderId(OrderId.from("ORDER_ID"))
                .orderType(OrderType.ORDER)
                .orderLines(orderLineModel()
                        .orderLineId(OrderLineId.from("ORDER_LINE_ID"))
                        .quantity(Quantity.of(10))
                        .skuId(SKU_ID))
                .transactionId(TransactionId.generate())
                .userId(UserId.from("USER_ID")).build();

        orderService.create(request);
    }

    @Test
    public void shouldAcceptNullGateForPreorder() {
        try {
            when(gateProperties.gateZone(GATE_ID)).thenReturn(Optional.of(ZoneId.from("ORDER_GATE")));
            when(configurationService.getConfigValue(ConfigKey.MAX_ORDER_SIZE)).thenReturn(200);

            CreateOrderRequest request = orderRequest()
                    .gateId(WRONG_GATE)
                    .orderId(OrderId.from("ORDER_ID"))
                    .orderType(OrderType.ORDER)
                    .orderLines(orderLineModel()
                            .orderLineId(OrderLineId.from("ORDER_LINE_ID"))
                            .quantity(Quantity.of(10))
                            .skuId(SkuId.from("SKU_ID")))
                    .transactionId(TransactionId.generate())
                    .userId(UserId.from("USER_ID")).build();

            orderService.create(request);
            Assert.fail();
        } catch (UnknownGateException uge) {
            Assertions.assertThat(uge.getGateId()).isEqualTo(WRONG_GATE);
        }
    }

}