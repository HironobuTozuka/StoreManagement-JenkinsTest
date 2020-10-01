package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inc.roms.rcs.vo.order.OrderStatus.NOT_STARTED;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPickService {

    private final MachineOperatorService machineOperatorService;
    private final TaskBundleService taskBundleService;
    private final OrderReservationService orderReservationService;
    private final OrderService orderService;
    private final ToteService toteService;
    private final OrderProgressService orderProgressService;

    @Retryable
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TaskBundle updatePickBundle(OrderId orderId, TaskBundle taskBundle) {
        Order order = orderService.getByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if(!order.getOrderStatus().equals(OrderStatus.READY_TO_BE_PICKED)) {
            throw new IllegalStateException();
        }
        TaskBundle picks = createPicks(order, taskBundle);
        orderProgressService.notStarted(order.getOrderId());
        machineOperatorService.updateBundle(picks);
        log.info("TaskBundle updated for order {}", order.getOrderId());
        return picks;
    }

    @Retryable
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TaskBundle pickOrder(OrderId orderId) {
        Order order = orderService.getByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if(!order.getOrderStatus().equals(OrderStatus.READY_TO_BE_PICKED)) {
            throw new IllegalStateException();
        }
        TaskBundle taskBundle = TaskBundle.pickBundle(orderId);
        TaskBundle picks = createPicks(order, taskBundle);
        machineOperatorService.sendBundle(picks);
        log.info("Sent picks for order {}", orderId);
        orderProgressService.notStarted(orderId);
        orderService.save(order);
        return picks;
    }

    public void cancelTaskBundlesForOrder(OrderId orderId) {
        List<TaskBundle> taskBundles = taskBundleService.findTaskBundles(orderId);
        taskBundles.forEach(machineOperatorService::cancelBundle);
    }

    private TaskBundle createPicks(Order order, TaskBundle taskBundle) {
        log.info("Creating picks for order {}", order.getOrderId());
        List<Pick> picks = createPicks(order);
        log.info("Created picks for order {}", order.getOrderId());
        taskBundle.setTasks(picks.stream().map(it -> (Task)it).collect(toList()));
        taskBundle.updateStatus();
        taskBundleService.save(taskBundle);
        return taskBundle;
    }

    private List<Pick> createPicks(Order order) {
        List<Reservation> reservations = orderReservationService.getReservationsFor(order);
        log.info("Found {} reservations for order {}", reservations.size(), order.getOrderId());
        return reservations.stream().map(it -> {
            Tote storageTote = toteService.getToteWithStorageReservation(it);
            Slot storageSlot = storageTote.getAllSlots().stream().filter(slot -> slot.getStorageInventory().getReservations().contains(it)).findFirst().orElseThrow();
            Tote deliveryTote = toteService.getToteWithDeliveryInventory(it.getDeliveryInventory());
            Slot deliverySlot = deliveryTote.getSlots().stream().filter(slot -> it.getDeliveryInventory().equals(slot.getDeliveryInventory())).findFirst().orElseThrow();
            log.info("Found storage slot: {} in storage tote: {}, delivery slot {} i delivery tote {}", storageSlot, storageTote, deliverySlot, deliveryTote);
            return new Pick(
                    storageTote.getToteId(),
                    storageSlot.getOrdinal(),
                    deliveryTote.getToteId(),
                    deliverySlot.getOrdinal(),
                    it.getSkuId(),
                    it.getQuantity(),
                    it
            );
        }).collect(toList());
    }
}
