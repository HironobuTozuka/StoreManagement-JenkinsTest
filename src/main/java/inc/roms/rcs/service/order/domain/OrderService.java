package inc.roms.rcs.service.order.domain;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.exception.OrderTooBigException;
import inc.roms.rcs.service.configuration.ConfigKey;
import inc.roms.rcs.service.configuration.ConfigurationService;
import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.order.config.GateProperties;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.service.order.domain.model.OrderTransaction;
import inc.roms.rcs.service.order.domain.model.TransactionType;
import inc.roms.rcs.service.order.domain.repository.OrderLineRepository;
import inc.roms.rcs.service.order.domain.repository.OrderRepository;
import inc.roms.rcs.service.order.domain.repository.OrderTransactionRepository;
import inc.roms.rcs.service.order.exception.*;
import inc.roms.rcs.service.order.request.*;
import inc.roms.rcs.service.order.response.BatchOrderActionResponse;
import inc.roms.rcs.service.order.response.OrderDetails;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final OrderLineRepository orderLineRepository;

    private final SkuService skuService;

    private final TaskBundleService taskBundleService;
    private final ReservationService reservationService;

    private final Clock clock;

    private final BusinessExceptions businessExceptions;

    private final ConfigurationService configurationService;
    private final FeatureFlagService featureFlagService;

    private final GateProperties gateProperties;

    @Transactional
    public Order create(CreateOrderRequest request) {
        validateOrderNotExists(request);
        validateOrderSize(request);
        validateGate(request);

        Order order = new Order();
        order.setGate(request.getGateId());
        order.setOrderId(request.getOrderId());
        order.setOrderLines(request.getOrderLines().stream().map(this::toOrderLine).filter(Objects::nonNull).collect(toList()));
        order.setOrderType(request.getOrderType());
        order.setPickupTime(request.getPickupTime());
        order.setUserId(request.getUserId());
        order.setOrderStatus(OrderStatus.NOT_STARTED);
        List<OrderTransaction> orderTransactions = createOrderTransactions(order, request);
        orderTransactionRepository.saveAll(orderTransactions);
        return orderRepository.save(order);
    }

    private void validateGate(CreateOrderRequest request) {
        if(request.getGateId() == null) return;
        gateProperties.gateZone(request.getGateId()).orElseThrow(() -> new UnknownGateException(request.getOrderId(), request.getGateId(), gateProperties));
    }

    private void validateOrderNotExists(CreateOrderRequest request) {
        orderRepository.findByOrderId(request.getOrderId()).ifPresent((o) -> {
            throw new OrderAlreadyExistsException(request.getOrderId());
        });
    }

    private void validateOrderSize(CreateOrderRequest request) {
        Integer maxOrderSize = configurationService.getConfigValue(ConfigKey.MAX_ORDER_SIZE);
        Optional<Quantity> totalNumberOfOrderedItems = request.getOrderLines().stream().map(OrderLineModel::getQuantity).reduce(Quantity::plus);
        if(totalNumberOfOrderedItems.isPresent() && totalNumberOfOrderedItems.get().gt(maxOrderSize)) {
            throw new OrderTooBigException(request.getOrderId(), maxOrderSize, totalNumberOfOrderedItems.get());
        }
    }

    private List<OrderTransaction> createOrderTransactions(Order order, CreateOrderRequest request) {
        List<OrderTransaction> transactions = Lists.newArrayList(new OrderTransaction(TransactionType.CREATE, order.getOrderId(), request.getTransactionId()));
        if (order.getOrderType() == OrderType.ORDER) {
            transactions.add(deliverOrderTransaction(order, request.getTransactionId()));
        }
        return transactions;
    }

    private OrderTransaction deliverOrderTransaction(Order order, TransactionId transactionId) {
        return new OrderTransaction(TransactionType.DELIVER, order.getOrderId(), transactionId);
    }

    private OrderLine toOrderLine(OrderLineModel orderLineModel) {
        if (orderLineModel.getSkuId() == null || Strings.isNullOrEmpty(orderLineModel.getSkuId().getSkuId()))
            return null;
        OrderLine orderLine = new OrderLine();
        if (orderLineModel.getOrderLineId() != null && !Strings.isNullOrEmpty(orderLineModel.getOrderLineId().getOrderLineId()))
            orderLine.setOrderLineId(orderLineModel.getOrderLineId());
        else
            orderLine.setOrderLineId(OrderLineId.generate());
        orderLine.setQuantity(orderLineModel.getQuantity());
        Sku sku = skuService.getReadySku(orderLineModel.getSkuId());
        orderLine.setSkuId(sku.getSkuId());
        return orderLine;
    }

    public OrderTransaction findTransaction(Order order, TransactionType transactionType) {
        return orderTransactionRepository.findFirstByOrderIdAndTransactionType(order.getOrderId(), transactionType);
    }

    public Optional<Order> getByOrderId(OrderId orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void save(OrderLine orderLine) {
        orderLineRepository.save(orderLine);
    }

    public void validateDeliver(DeliverOrderRequest request) {
        Order order = getByOrderId(request.getOrderId()).orElseThrow(() -> businessExceptions.orderNotFoundException(request.getOrderId()));
        if (order.getOrderStatus().equals(OrderStatus.ABANDONED)) {
            throw new OrderAbandonedException(request.getOrderId(), order.getOrderStatus());
        } else if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderAbandonedException(request.getOrderId(), order.getOrderStatus());
        } else if (order.getOrderStatus().equals(OrderStatus.DISPOSED)) {
            throw new OrderAbandonedException(request.getOrderId(), order.getOrderStatus());
        } else if (OrderStatus.COLLECTED.equals(order.getOrderStatus())) {
            throw new OrderAlreadyDeliveredException(request.getOrderId());
        } else if (!order.getOrderStatus().equals(OrderStatus.PREORDER_READY) && !featureFlagService.isDelayedPickPreorder()) {
                log.warn("Preorder {} was chosen for delivery, but it's not ready yet!", request.getOrderId());
                throw new PreorderCannotBeDeliveredException(request.getOrderId());
        }
    }

    public Order updatePickGate(DeliverOrderRequest request) {
        Order order = getByOrderId(request.getOrderId()).orElseThrow(() -> businessExceptions.orderNotFoundException(request.getOrderId()));
        order.setGate(request.getGateId());
        OrderTransaction orderTransactions = deliverOrderTransaction(order, request.getTransactionId());
        orderTransactionRepository.save(orderTransactions);
        save(order);
        return order;
    }

    public List<OrderDetails> list(ListOrderRequest request) {
        List<Order> orders = orderRepository.findAll(buildSpec(request));
        return orders.stream()
                .map(OrderDetails.Builder::new)
                .peek(this::fetchTotes)
                .map(OrderDetails.Builder::build)
                .filter(byDeliveryTotes(request))
                .filter(byStorageTotes(request))
                .collect(toList());
    }

    private void fetchTotes(OrderDetails.Builder orderDetailsBuilder) {
        List<TaskBundle> taskBundles = taskBundleService.findTaskBundles(orderDetailsBuilder.getOrderId());
        List<Pick> picks = taskBundles
                .stream()
                .flatMap(it -> it.getTasks().stream())
                .filter(it -> it instanceof Pick)
                .map(it -> (Pick) it)
                .collect(toList());

        List<ToteId> deliveryTotes = picks.stream().map(Pick::getDestinationToteId).distinct().collect(toList());

        orderDetailsBuilder.deliveryTotes(deliveryTotes);

        picks.stream()
                .collect(
                        groupingBy(
                                orderLineId(),
                                collectSourceToteIds()
                        ))
                .forEach(orderDetailsBuilder::storageTotes);
    }

    private java.util.function.Predicate<OrderDetails> byStorageTotes(ListOrderRequest request) {
        return orderDetails -> request.getStorageToteId() == null || orderDetails.getOrderLines().stream().anyMatch(orderLineDetails -> orderLineDetails.getStorageTotes().contains(request.getStorageToteId()));
    }

    private java.util.function.Predicate<OrderDetails> byDeliveryTotes(ListOrderRequest request) {
        return orderDetails -> request.getDeliveryToteId() == null || orderDetails.getDeliveryTotes().contains(request.getDeliveryToteId());
    }

    private Collector<Pick, ?, List<ToteId>> collectSourceToteIds() {
        return Collectors.mapping(Pick::getSourceToteId, Collectors.toList());
    }

    private Function<Pick, OrderLineId> orderLineId() {
        return pick -> pick.getReservation().getOrderLine().getOrderLineId();
    }

    static Specification<Order> buildSpec(ListOrderRequest r) {
        return (order, query, criteriaBuilder) -> {

            List<Predicate> restrictions = new ArrayList<>();
            if (r.getSkuId() != null) {
                restrictions.add(criteriaBuilder.equal(order.join("orderLines").get("skuId"), r.getSkuId()));
            }
            if (r.getOrderId() != null) {
                restrictions.add(criteriaBuilder.equal(order.get("orderId"), r.getOrderId()));
            }
            if (r.getOrderStatus() != null) {
                restrictions.add(criteriaBuilder.equal(order.get("orderStatus"), r.getOrderStatus()));
            }
            return criteriaBuilder.and(restrictions.toArray(new Predicate[0]));
        };
    }

    public BatchOrderActionResponse delete(BatchOrderActionRequest batchOrderActionRequest) {
        List<Order> orders = orderRepository.findAllByOrderIdIn(batchOrderActionRequest.getOrderIds());
        List<OrderId> success = new ArrayList<>();
        List<OrderId> failed = new ArrayList<>();

        orders.forEach(order -> {
            try {
                List<TaskBundle> taskBundles = taskBundleService.findTaskBundles(order.getOrderId());
                taskBundleService.delete(taskBundles);
                List<Reservation> reservations = reservationService.getReservationsFor(order);
                reservationService.deleteReservations(reservations);
                OrderTransaction transaction = new OrderTransaction(TransactionType.DELETE, order.getOrderId(), batchOrderActionRequest.getTransactionId());
                orderTransactionRepository.save(transaction);
                orderRepository.delete(order);
                success.add(order.getOrderId());
            } catch (Exception e) {
                failed.add(order.getOrderId());
            }
        });
        return BatchOrderActionResponse.builder()
                .success(success)
                .failed(failed)
                .build();
    }
}
