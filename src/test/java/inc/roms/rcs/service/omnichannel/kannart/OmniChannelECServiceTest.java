package inc.roms.rcs.service.omnichannel.kannart;

import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.omnichannel.kannart.model.OrderStatusChangedRequest;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderTransaction;
import inc.roms.rcs.service.order.domain.model.TransactionType;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OmniChannelECServiceTest {

    public static final TransactionId CREATE_TRANSACTION_ID = TransactionId.generate();
    public static final OrderId ORDER_ID = OrderId.generate();

    private final OmniChannelECClient omniChannelECClient = mock(OmniChannelECClient.class);
    private final SkuService skuService = mock(SkuService.class);
    private final OrderService orderService = mock(OrderService.class);

    private final IssueService issueService = mock(IssueService.class);
    private final IssueFactory issueFactory = mock(IssueFactory.class);

    private final OmniChannelECService omniChannelECService = new OmniChannelECService(omniChannelECClient, skuService, orderService, issueService, issueFactory);

    @Test
    public void shouldReportOrderStatusChangedWithTransactionId() {
        Order order = givenOrder();
        OrderTransaction orderTransaction = givenOrderTransaction(order);

        omniChannelECService.orderStatusChanged(order);

        ArgumentCaptor<OrderStatusChangedRequest> argument = ArgumentCaptor.forClass(OrderStatusChangedRequest.class);
        verify(omniChannelECClient).send(argument.capture());

        assertThat(argument.getValue().getTransactionId()).isEqualTo(orderTransaction.getTransactionId());
    }

    private Order givenOrder() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PICKUP_GATE);
        order.setOrderId(ORDER_ID);
        order.setId(1);
        when(orderService.getByOrderId(ORDER_ID)).thenReturn(Optional.of(order));
        return order;
    }

    private OrderTransaction givenOrderTransaction(Order order) {
        OrderTransaction orderTransaction = new OrderTransaction();
        orderTransaction.setTransactionType(TransactionType.DELIVER);
        orderTransaction.setOrderId(order.getOrderId());
        orderTransaction.setTransactionId(CREATE_TRANSACTION_ID);
        when(orderService.findTransaction(order, orderTransaction.getTransactionType())).thenReturn(orderTransaction);
        return orderTransaction;
    }
}