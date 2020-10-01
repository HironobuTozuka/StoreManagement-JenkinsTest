package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.builders.TaskBuilder;
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
import inc.roms.rcs.service.task.domain.model.*;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static inc.roms.rcs.builders.OrderBuilder.order;
import static inc.roms.rcs.builders.TaskBundleBuilder.taskBundle;
import static inc.roms.rcs.service.task.domain.model.TaskBundleStatus.COMPLETED;
import static inc.roms.rcs.service.task.domain.model.TaskBundleStatus.IN_PROGRESS;
import static inc.roms.rcs.vo.order.OrderStatus.*;
import static inc.roms.rcs.vo.order.OrderType.ORDER;
import static inc.roms.rcs.vo.order.OrderType.PREORDER;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderTaskBundleListenerTest {

    public static final OrderId ORDER_ID = OrderId.generate();
    public static final ToteId DEST_TOTE_ID = ToteId.from("DEST");

    private final OrderService orderService = mock(OrderService.class);
    private final OrderReservationService reservationService = mock(OrderReservationService.class);
    private final OmniChannelService omniChannelService = mock(OmniChannelService.class);
    private final MachineOperatorService machineOperatorService = mock(MachineOperatorService.class);
    private final IssueService issueService = mock(IssueService.class);
    private final IssueFactory issueFactory = mock(IssueFactory.class);
    private final FeatureFlagService featureFlagService = mock(FeatureFlagService.class);
    private final AsyncOrderPickService orderPickService = mock(AsyncOrderPickService.class);
    private final ToteService toteService = mock(ToteService.class);
    private final OrderProgressService orderProgressService = mock(OrderProgressService.class);

    private final OrderTaskBundleListener orderTaskBundleListener = new OrderTaskBundleListener(
            orderService,
            reservationService,
            orderProgressService,
            omniChannelService,
            machineOperatorService,
            issueService,
            issueFactory,
            featureFlagService,
            orderPickService,
            toteService
    );

    @Test
    public void shouldUpdateOrderStatusToPicking() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(IN_PROGRESS)
                .type(TaskBundleType.PICKING)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderStatus(NOT_STARTED)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(orderProgressService).picking(order.getOrderId());
    }

    @Test
    public void shouldUpdateOrderStatusToPreorderReady() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(COMPLETED)
                .type(TaskBundleType.PICKING)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(PREORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(orderProgressService).preorderReady(order.getOrderId());
    }

    @Test
    public void shouldSendPreOrderToStaging() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(COMPLETED)
                .type(TaskBundleType.PICKING)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(PREORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(machineOperatorService).sendToStaging(order);
    }

    @Test
    public void shouldSendOrderToPickGate() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(COMPLETED)
                .type(TaskBundleType.PICKING)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(machineOperatorService).sendToGate(order);
    }

    @Test
    public void shouldCreateAndReportIssueOnDeliveryTaskBundleFailure() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(TaskBundleStatus.FAILED)
                .type(TaskBundleType.DELIVERY)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PREORDER_READY)
                .build();

        CreateIssueRequest issueRequest = CreateIssueRequest.issue().build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));
        when(issueFactory.cannotDeliverPreorder(eq(ORDER_ID))).thenReturn(issueRequest);

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(omniChannelService).orderStatusChanged(order);
        verify(issueService).createAndReport(issueRequest);
    }

    @Test
    public void shouldSendOrderStatusUpdate() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(COMPLETED)
                .type(TaskBundleType.PICKING)
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(omniChannelService).orderStatusChanged(order);
    }

    @Test
    public void shouldCancelRemainingPicksIfOneFailed() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(TaskBundleStatus.FAILED)
                .type(TaskBundleType.PICKING)
                .tasks(
                        TaskBuilder
                                .pick()
                                .taskStatus(TaskStatus.FAILED)
                                .failReason(FailReason.OTHER_PICK_ERROR),
                        TaskBuilder
                                .pick()
                                .taskStatus(TaskStatus.CREATED)
                )
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(machineOperatorService).cancelBundle(taskBundle);
    }

    @Test
    public void shouldNotCancelTaskBundleIfNoTasksWereInCreatedStatus() {
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(TaskBundleStatus.FAILED)
                .type(TaskBundleType.PICKING)
                .tasks(
                        TaskBuilder
                                .pick()
                                .taskStatus(TaskStatus.FAILED)
                                .failReason(FailReason.ROBOT_ERROR),
                        TaskBuilder
                                .pick()
                                .taskStatus(TaskStatus.COMPLETED)
                )
                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(machineOperatorService, times(0)).cancelBundle(taskBundle);
    }

    @Test
    public void shouldUpdateTasksInTaskBundleOnDeliveryToteFailure() {
        CreateIssueRequest issueRequest = CreateIssueRequest.issue().build();
        when(issueFactory.destToteFailure(DEST_TOTE_ID)).thenReturn(issueRequest);
        TaskBundle taskBundle = taskBundle()
                .orderId(ORDER_ID)
                .status(TaskBundleStatus.FAILED)
                .type(TaskBundleType.PICKING)
                .tasks(
                        TaskBuilder
                                .pick()
                                .destinationToteId(DEST_TOTE_ID)
                                .failReason(FailReason.DEST_TOTE_ERROR)
                                .taskStatus(TaskStatus.FAILED),
                        TaskBuilder
                                .pick()
                                .taskStatus(TaskStatus.CREATED)
                )

                .build();

        Order order = order()
                .orderId(ORDER_ID)
                .orderType(ORDER)
                .orderStatus(PICKING)
                .build();

        when(orderService.getByOrderId(eq(ORDER_ID))).thenReturn(Optional.of(order));

        orderTaskBundleListener.onUpdate(taskBundle);

        verify(reservationService).reserveDeliverySlots(order);
        verify(orderPickService).updatePickBundle(order.getOrderId(), taskBundle);
        verify(issueService).createAndReport(eq(issueRequest));
        verify(toteService).markAsFailing(DEST_TOTE_ID, ToteStatus.IN_ERROR);
    }
}