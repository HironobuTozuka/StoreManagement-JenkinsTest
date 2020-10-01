package inc.roms.rcs.service.order;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.order.domain.OrderProgressService;
import inc.roms.rcs.service.order.domain.OrderReservationService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.request.DeliverOrderRequest;
import inc.roms.rcs.service.order.request.ListOrderRequest;
import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.order.response.OrderActionResponse;
import inc.roms.rcs.service.order.response.OrderActionResponseDetails;
import inc.roms.rcs.service.order.response.OrderDetails;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.zones.ZoneFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TxOrderManagementInternalService {
    private final MachineOperatorService machineOperatorService;
    private final OrderReservationService orderReservationService;
    private final OrderService orderService;
    private final OrderProgressService orderProgressService;
    private final ToteService toteService;

    @Transactional
    public Order createInTransaction(CreateOrderRequest request) {
        log.info("Creating order {}: save", request.getOrderId());
        Order order = orderService.create(request);
        log.info("Creating order {}: make reservations", request.getOrderId());
        orderReservationService.makeReservationsFor(order);
        log.info("Creating order {}: plan fulfillment", request.getOrderId());
        orderReservationService.reserveDeliverySlots(order);
        log.info("Creating order {}: done", request.getOrderId());
        return order;
    }

    @Transactional
    public Order deliver(DeliverOrderRequest request) {
        orderService.validateDeliver(request);
        Order order = orderService.updatePickGate(request);
        machineOperatorService.sendToGate(order);
        return order;
    }

    @Transactional
    public Order retrieve(DeliverOrderRequest request) {
        Order order = orderService.updatePickGate(request);
        machineOperatorService.sendToGate(order);
        return order;
    }

    @Transactional
    public void retrieveByTote(ToteRequest toteRequest) {
        Tote tote = toteService.findToteByToteId(toteRequest.getToteId()).orElseThrow(() -> new ToteNotFoundException(toteRequest.getToteId()));
        Optional<OrderId> orderId = tote.getSlots().stream()
                .filter(it -> it.getDeliveryInventory() != null)
                .filter(it -> it.getDeliveryInventory().getOrderId() != null)
                .map(it -> it.getDeliveryInventory().getOrderId())
                .findFirst();

        if(orderId.isPresent()) {
            DeliverOrderRequest request = new DeliverOrderRequest();
            request.setTransactionId(TransactionId.generate());
            request.setOrderId(orderId.get());
            request.setGateId(GateId.LOADING_GATE);
            retrieve(request);
        } else {
            machineOperatorService.moveTote(toteRequest.getToteId(), ZoneFunction.LOADING_GATE);
        }
    }

    @Transactional
    public void updatePickGate(DeliverOrderRequest request) {
        orderService.validateDeliver(request);
        orderService.updatePickGate(request);
    }

    public OrderActionResponse dispose(OrderActionRequest request) {
        orderProgressService.disposed(request.getOrderId());
        return OrderActionResponse.builder()
                .details(OrderActionResponseDetails
                        .responseDetails()
                        .orderId(request.getOrderId()).build())
                .responseCode(ResponseCode.ACCEPTED)
                .build();
    }
    public OrderActionResponse cancel(OrderActionRequest request) {
        orderProgressService.cancelled(request.getOrderId());
        return OrderActionResponse.builder()
                .details(OrderActionResponseDetails
                        .responseDetails()
                        .orderId(request.getOrderId()).build())
                .responseCode(ResponseCode.ACCEPTED)
                .build();
    }

    public List<OrderDetails> list(ListOrderRequest request) {
        return orderService.list(request);
    }

}
