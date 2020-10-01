package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.order.domain.AsyncOrderPickService;
import inc.roms.rcs.service.order.domain.OrderProgressService;
import inc.roms.rcs.service.order.domain.OrderReservationService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.service.task.domain.model.*;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static inc.roms.rcs.service.task.domain.model.TaskBundleStatus.*;
import static inc.roms.rcs.vo.order.OrderStatus.FAILED;
import static inc.roms.rcs.vo.order.OrderStatus.*;
import static inc.roms.rcs.vo.order.OrderType.PREORDER;
import static inc.roms.rcs.vo.order.OrderType.REPLENISHMENT;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTaskBundleListener implements TaskBundleListener {

    private final OrderService orderService;
    private final OrderReservationService orderReservationService;
    private final OrderProgressService orderProgressService;
    private final @Qualifier("AsyncOmniChannelService") OmniChannelService omniChannelService;
    private final MachineOperatorService machineOperatorService;
    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final FeatureFlagService featureFlagService;
    private final AsyncOrderPickService orderPickService;
    private final ToteService toteService;

    @Override
    public void onUpdate(TaskBundle taskBundle) {
        if (taskBundle.getOrderId() == null) return;

        Order order = orderService.getByOrderId(taskBundle.getOrderId()).orElseThrow(() -> new OrderNotFoundException(taskBundle.getOrderId()));

        if (isCompleted(taskBundle)) {
            onComplete(order, taskBundle);
        } else if (isInProgress(taskBundle)) {
            onInProgress(order, taskBundle);
        } else if (isFailed(taskBundle)) {
            onFailed(order, taskBundle);
        } else if (isCancelled(taskBundle)) {
            onCancelled(order, taskBundle);
        }

        omniChannelService.orderStatusChanged(order);
        orderService.save(order);
    }

    private void onCancelled(Order order, TaskBundle taskBundle) {
        handleUnrecoverableFailure(order, taskBundle);
    }

    private void onFailed(Order order, TaskBundle taskBundle) {
        if (taskBundle.getFailReason().isPresent()
                && FailReason.DEST_TOTE_ERROR.equals(taskBundle.getFailReason().get())
                && isFirstTask(taskBundle)) {
            onDestToteFailed(order, taskBundle);
        } else if (IN_PICKUP_GATE.equals(order.getOrderStatus()) && TaskBundleType.DELIVERY.equals(taskBundle.getType())) {
            onOrderCollectionFailed(order, taskBundle);
        } else if (TaskBundleType.DELIVERY.equals(taskBundle.getType())) {
            onOrderDeliveryFailed(order, taskBundle);
        } else if (TaskBundleType.PICKING.equals(taskBundle.getType())) {
            onPickingFailed(order, taskBundle);
        } else {
            onOtherReasonFailed(order, taskBundle);
        }
    }

    private boolean isFirstTask(TaskBundle taskBundle) {
        return taskBundle.getTasks().stream().filter(it -> it.getStatus() != TaskStatus.CREATED).count() == 1;
    }

    private void onDestToteFailed(Order order, TaskBundle taskBundle) {
        log.info("Completing order {}: dest tote failure!", order.getOrderId());
        ToteId toteId = taskBundle.getTasks().stream()
                .filter(it -> TaskStatus.FAILED.equals(it.getStatus()))
                .filter(it -> it instanceof Pick)
                .map(it -> (Pick) it)
                .findFirst().orElseThrow().getDestinationToteId();

        CreateIssueRequest createIssueRequest = issueFactory.destToteFailure(toteId);
        issueService.createAndReport(createIssueRequest); //async
        toteService.markAsFailing(toteId, ToteStatus.IN_ERROR);
        try {
            orderReservationService.reserveDeliverySlots(order);
            orderPickService.updatePickBundle(order.getOrderId(), taskBundle);
        } catch (NoEmptyTotesException exception) {
            createIssueRequest = issueFactory.noEmptyTotes();
            issueService.createAndReport(createIssueRequest);
            machineOperatorService.cancelBundle(taskBundle);
        }
    }

    private void onOrderCollectionFailed(Order order, TaskBundle taskBundle) {
        log.info("Completing order {}: collection failed", order.getOrderId());
        orderProgressService.notCollected(order.getOrderId());
        handleUnrecoverableFailure(order, taskBundle);
    }

    private void onOrderDeliveryFailed(Order order, TaskBundle taskBundle) {
        log.info("Completing order {}: couldn't deliver to pickup gate", order.getOrderId());
        CreateIssueRequest issue = issueFactory.cannotDeliverPreorder(order.getOrderId());
        issueService.createAndReport(issue);
        orderProgressService.failed(order.getOrderId());
        handleUnrecoverableFailure(order, taskBundle);
    }

    private void onPickingFailed(Order order, TaskBundle taskBundle) {
        log.info("Completing order {}, picking failed", order.getOrderId());
        orderProgressService.failed(order.getOrderId());
        handleUnrecoverableFailure(order, taskBundle);
    }

    private void onOtherReasonFailed(Order order, TaskBundle taskBundle) {
        log.info("Completing order {}: fulfillment failed", order.getOrderId());
        orderProgressService.failed(order.getOrderId());
        handleUnrecoverableFailure(order, taskBundle);
    }

    private void onInProgress(Order order, TaskBundle taskBundle) {
        if (TaskBundleType.PICKING.equals(taskBundle.getType())) {
            log.info("Completing order {}: picking in progress", order.getOrderId());
            orderProgressService.picking(order.getOrderId());
        } else if (TaskBundleType.DELIVERY.equals(taskBundle.getType()) && isNotFailed(order)) {
            log.info("Completing order {}: delivery in progress", order.getOrderId());
            orderProgressService.delivered(order.getOrderId());
        }
    }

    private boolean isNotFailed(Order order) {
        return !List.of(FAILED, DISPOSED, OrderStatus.CANCELLED).contains(order.getOrderStatus());
    }

    private void onComplete(Order order, TaskBundle taskBundle) {
        if (TaskBundleType.PICKING.equals(taskBundle.getType())) {
            log.info("Completing order {}: picking completed", order.getOrderId());
            pickingCompleted(order);
        } else if (TaskBundleType.DELIVERY.equals(taskBundle.getType()) && isNotFailed(order)) {
            log.info("Completing order {}: delivery completed", order.getOrderId());
            pickupCompleted(order);
        }
    }

    private void pickupCompleted(Order order) {
        orderProgressService.collected(order.getOrderId());
    }

    private void pickingCompleted(Order order) {
        if (PREORDER.equals(order.getOrderType()) && !featureFlagService.isDelayedPickPreorder()) {
            log.info("Completing order {}: Sending preorder to staging", order.getOrderId());
            orderProgressService.preorderReady(order.getOrderId());
            machineOperatorService.sendToStaging(order);
        } else if (REPLENISHMENT.equals(order.getOrderType())) {
            log.info("Completing order {}: Sending preorder to staging", order.getOrderId());
            orderProgressService.preorderReady(order.getOrderId());
            machineOperatorService.sendToStaging(order);
        } else {
            log.info("Completing order {}: Sending order to pickup gate", order.getOrderId());
            orderProgressService.goingToPickupGate(order.getOrderId());
            machineOperatorService.sendToGate(order);
        }
    }

    private boolean isFailed(TaskBundle taskBundle) {
        return TaskBundleStatus.FAILED.equals(taskBundle.getStatus()) || FAILING.equals(taskBundle.getStatus());
    }

    private boolean isCancelled(TaskBundle taskBundle) {
        return TaskBundleStatus.CANCELLED.equals(taskBundle.getStatus());
    }

    private boolean isInProgress(TaskBundle taskBundle) {
        return IN_PROGRESS.equals(taskBundle.getStatus());
    }

    private boolean isCompleted(TaskBundle taskBundle) {
        return COMPLETED.equals(taskBundle.getStatus());
    }

    private void handleUnrecoverableFailure(Order order, TaskBundle taskBundle) {
        if (noTaskInStatus(taskBundle, TaskStatus.CREATED)) {
            if(order.getGate() != null) {
                machineOperatorService.sendToGate(order);
            } else {
                machineOperatorService.sendToStaging(order);
            }
        } else if (noTaskInStatus(taskBundle, TaskStatus.CANCELLED)) {
            machineOperatorService.cancelBundle(taskBundle);
        }
    }

    private boolean noTaskInStatus(TaskBundle taskBundle, TaskStatus created) {
        return taskBundle.getTasks().stream().noneMatch(it -> it.getStatus().equals(created));
    }

}
