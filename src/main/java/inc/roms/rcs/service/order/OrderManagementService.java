package inc.roms.rcs.service.order;

import inc.roms.rcs.exception.BatchOrderActionFailedException;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.order.domain.AsyncOrderPickService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.service.order.request.*;
import inc.roms.rcs.service.order.response.*;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static inc.roms.rcs.service.order.response.CreateOrderResponse.createOrderResponse;
import static inc.roms.rcs.service.order.response.CreateOrderResponseDetails.details;
import static inc.roms.rcs.vo.order.OrderType.ORDER;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderManagementService {

    private final TxOrderManagementInternalService txOrderService;
    private final AsyncOrderPickService orderPickService;
    private final FeatureFlagService featureFlagService;

    public synchronized CreateOrderResponse create(CreateOrderRequest request) {
        log.info("Creating order {}: creation start", request.getOrderId());
        Order order = txOrderService.createInTransaction(request);
        log.info("Creating order {}: created, scheduling picking", request.getOrderId());
        CompletableFuture<TaskBundle> pickOrderFuture = null;
        if (ORDER.equals(request.getOrderType()) || !featureFlagService.isDelayedPickPreorder()) {
            pickOrderFuture = orderPickService.pickOrder(order.getOrderId());
            log.info("Creating order {}: pick creation scheduled", request.getOrderId());
        }
        return createOrderResponse()
                .responseDetails(details()
                        .pickFuture(pickOrderFuture)
                        .eta(calculateEta(order))
                        .gateId(order.getGate()))
                .build();

    }

    private LocalDateTime calculateEta(Order order) {
        return LocalDateTime.now(ZoneOffset.UTC).plus(numberOfItemsIn(order) * 15 + 15, ChronoUnit.SECONDS);
    }

    private Integer numberOfItemsIn(Order order) {
        return order.getOrderLines().stream()
                .map(OrderLine::getQuantity)
                .reduce(Quantity::plus)
                .orElse(Quantity.of(0))
                .getQuantity();
    }

    public DeliverOrderResponse deliver(DeliverOrderRequest request) {
        if (featureFlagService.isDelayedPickPreorder()) {
            txOrderService.updatePickGate(request);
            orderPickService.pickOrder(request.getOrderId());
        } else {
            txOrderService.deliver(request);
        }
        DeliverOrderResponse deliverOrderResponse = new DeliverOrderResponse();
        deliverOrderResponse.setResponseCode(ResponseCode.ACCEPTED);
        return deliverOrderResponse;
    }

    public DeliverOrderResponse retrieve(DeliverOrderRequest request) {
        txOrderService.retrieve(request);
        DeliverOrderResponse deliverOrderResponse = new DeliverOrderResponse();
        deliverOrderResponse.setResponseCode(ResponseCode.ACCEPTED);
        return deliverOrderResponse;
    }

    public DeliverOrderResponse retrieveByTote(ToteRequest toteRequest) {
        txOrderService.retrieveByTote(toteRequest);
        DeliverOrderResponse deliverOrderResponse = new DeliverOrderResponse();
        deliverOrderResponse.setResponseCode(ResponseCode.ACCEPTED);
        return deliverOrderResponse;
    }

    public OrderActionResponse cancel(OrderActionRequest request) {
        OrderActionResponse dispose = txOrderService.cancel(request);
        orderPickService.cancelOrder(request);
        return dispose;
    }

    public BatchOrderActionResponse batchDispose(BatchOrderActionRequest batchRequest) {
        return executeBatchAction(batchRequest, this::dispose);
    }

    public OrderActionResponse dispose(OrderActionRequest request) {
        return txOrderService.dispose(request);
    }

    public BatchOrderActionResponse batchCancel(BatchOrderActionRequest request) {
        return executeBatchAction(request, this::cancel);
    }

    private BatchOrderActionResponse executeBatchAction(BatchOrderActionRequest batchRequest, Function<OrderActionRequest, OrderActionResponse> action) {
        List<OrderActionRequest> requests = batchRequest.getOrderIds()
                .stream()
                .map(OrderActionRequest::new).collect(toList());

        List<OrderActionResponse> responses = new ArrayList<>();
        List<OrderNotFoundException> orderNotFoundExceptions = new ArrayList<>();

        for (OrderActionRequest request : requests) {
            try {
                responses.add(this.dispose(request));
            } catch (OrderNotFoundException orderNotFoundException) {
                orderNotFoundExceptions.add(orderNotFoundException);
            }
        }

        if (responses.isEmpty()) {
            List<OrderId> notFound = orderNotFoundExceptions.stream().map(OrderNotFoundException::getOrderId).collect(toList());
            throw new BatchOrderActionFailedException(notFound);
        } else {
            List<OrderId> notFound = orderNotFoundExceptions.stream().map(OrderNotFoundException::getOrderId).collect(toList());
            List<OrderId> success = responses.stream().map(OrderActionResponse::getDetails).map(OrderActionResponseDetails::getOrderId).collect(toList());
            return BatchOrderActionResponse.builder().success(success).failed(notFound).build();
        }
    }


    public OrderActionResponse pickOrder(OrderId orderId) {
        OrderActionResponse.OrderActionResponseBuilder orderActionResponse = OrderActionResponse.builder();
        try {
            orderPickService.pickOrder(orderId);
            orderActionResponse.responseCode(ResponseCode.ACCEPTED);
            return orderActionResponse.build();
        } catch (Exception ex) {
            log.error("Error during scheduled picking!", ex);
            orderActionResponse.responseCode(ResponseCode.REJECTED);
            return orderActionResponse.build();
        }
    }

    public ListOrderResponse list(ListOrderRequest request) {
        List<OrderDetails> orders = txOrderService.list(request);
        return ListOrderResponse.builder().orders(orders).build();
    }
}
