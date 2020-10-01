package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.service.machineoperator.exception.MachineOperatorException;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.order.OrderId;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AsyncOrderPickServiceTest {

    public static final OrderId ORDER_ID = OrderId.from("ORDER_TO_TEST");
    private final OrderProgressService orderProgressService = mock(OrderProgressService.class);
    private final OrderPickService orderPickService = mock(OrderPickService.class);

    private final AsyncOrderPickService asyncOrderPickService = new AsyncOrderPickService(orderPickService, orderProgressService);

    @Test
    public void shouldReturnTaskBundleIfPicksAreSentOK() throws ExecutionException, InterruptedException {
        //given
        TaskBundle taskBundle = mock(TaskBundle.class);
        when(orderPickService.pickOrder(ORDER_ID)).thenReturn(taskBundle);

        //when
        CompletableFuture<TaskBundle> taskBundleCompletableFuture = asyncOrderPickService.pickOrder(ORDER_ID);

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(taskBundleCompletableFuture::isDone);

        //then
        assertThat(taskBundleCompletableFuture.get()).isEqualTo(taskBundle);
    }

    @Test
    public void shouldFailOrderIfPicksCouldntBeSent() {
        //given
        when(orderPickService.pickOrder(ORDER_ID)).thenThrow(new MachineOperatorException(new RuntimeException()));

        //when
        CompletableFuture<TaskBundle> taskBundleCompletableFuture = asyncOrderPickService.pickOrder(ORDER_ID);

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(taskBundleCompletableFuture::isDone);

        //then
        verify(orderProgressService).failNotStarted(ORDER_ID);
    }

}