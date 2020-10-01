package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncOrderPickService {

    private final OrderPickService orderPickService;
    private final OrderProgressService orderProgressService;

    @Async
    public CompletableFuture<TaskBundle> updatePickBundle(OrderId orderId, TaskBundle taskBundle) {
        return safeExecute(() -> orderPickService.updatePickBundle(orderId, taskBundle), orderId);
    }

    @Async
    public CompletableFuture<TaskBundle> pickOrder(OrderId orderId) {
        return safeExecute(() -> orderPickService.pickOrder(orderId), orderId);
    }

    @Async
    public void cancelOrder(OrderActionRequest request) {
        orderPickService.cancelTaskBundlesForOrder(request.getOrderId());
    }

    private CompletableFuture<TaskBundle> safeExecute(Callable<TaskBundle> executable, OrderId orderId) {
        try {
            return CompletableFuture.completedFuture(executable.call());
        } catch (Exception ex) {
            orderProgressService.failNotStarted(orderId);
            return CompletableFuture.failedFuture(ex);
        }
    }


}
