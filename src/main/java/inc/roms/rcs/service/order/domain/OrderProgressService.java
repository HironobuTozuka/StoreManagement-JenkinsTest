package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderProgressService {

    private final OrderService orderService;
    private final @Qualifier("AsyncOmniChannelService") OmniChannelService omniChannelService;
    private final Clock clock;
    private final OrderReservationService orderReservationService;

    public void failNotStarted(OrderId orderId) {
        progressTo(OrderStatus.FAILED, orderId);
        orderReservationService.removeStorageReservations(orderId);
    }

    public void failed(OrderId orderId) {
        progressTo(OrderStatus.FAILED, orderId);
    }

    public void delivered(OrderId orderId) {
        progressTo(OrderStatus.IN_PICKUP_GATE, orderId);
    }

    public void collected(OrderId orderId) {
        progressTo(OrderStatus.COLLECTED, orderId);
    }

    public void picking(OrderId orderId) {
        progressTo(OrderStatus.PICKING, orderId);
    }

    public void removed(OrderId orderId) {
        progressTo(OrderStatus.REMOVED, orderId);
    }

    private void progressTo(OrderStatus status, OrderId orderId) {
        Order order = orderService.getByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if (status.equals(OrderStatus.IN_PICKUP_GATE)) {
            order.setDeliveredAt(LocalDateTime.now(clock));
        } else if (status.equals(OrderStatus.COLLECTED)) {
            order.setCollectedAt(LocalDateTime.now(clock));
        }
        order.setOrderStatus(status);
        omniChannelService.orderStatusChanged(order);
        orderService.save(order);
    }

    public void notCollected(OrderId orderId) {
        progressTo(OrderStatus.NOT_COLLECTED, orderId);
    }

    public void preorderReady(OrderId orderId) {
        progressTo(OrderStatus.PREORDER_READY, orderId);
    }

    public void goingToPickupGate(OrderId orderId) {
        progressTo(OrderStatus.GOING_TO_PICKUP_GATE, orderId);
    }

    public void notStarted(OrderId orderId) {
        progressTo(OrderStatus.NOT_STARTED, orderId);
    }

    public void disposed(OrderId orderId) {
        progressTo(OrderStatus.DISPOSED, orderId);
    }

    public void cancelled(OrderId orderId) {
        progressTo(OrderStatus.CANCELLED, orderId);
    }
}
