package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.task.domain.model.Delivery;
import inc.roms.rcs.service.task.domain.model.Move;
import inc.roms.rcs.service.task.domain.model.Pick;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteTaskBundleFactory {

    private final ToteService toteService;

    public ExecuteTaskBundleRequest convertToRequest(TaskBundle taskBundle) {
        log.info("Task bundle prepared to be send: {}", taskBundle);
        ExecuteTaskBundleRequest executeTaskBundleRequest = new ExecuteTaskBundleRequest();
        executeTaskBundleRequest.setTaskBundleId(taskBundle.getTaskBundleId());
        executeTaskBundleRequest.addAll(taskBundle.getTasks().stream().filter(it -> it instanceof Pick).map(it -> (Pick) it).map(this::convert).collect(toList()));
        executeTaskBundleRequest.addAll(taskBundle.getTasks().stream().filter(it -> it instanceof Move).map(it -> (Move) it).map(this::convert).collect(toList()));
        executeTaskBundleRequest.addAll(taskBundle.getTasks().stream().filter(it -> it instanceof Delivery).map(it -> (Delivery) it).map(this::convert).collect(toList()));
        return executeTaskBundleRequest;
    }

    private PickRequest convert(Pick pick) {
        PickRequest pickRequest = new PickRequest();
        ToteData destTote = new ToteData();
        destTote.setSlotId(pick.getDestinationSlotOrdinal());
        destTote.setToteId(pick.getDestinationToteId());
        ToteData sourceTote = new ToteData();
        sourceTote.setSlotId(pick.getSourceSlotOrdinal());
        sourceTote.setToteId(pick.getSourceToteId());
        pickRequest.setProductBarcode(pick.getSkuId());
        pickRequest.setQuantity(pick.getQuantity());
        pickRequest.setDestTote(destTote);
        pickRequest.setSourceTote(sourceTote);
        pickRequest.setTaskId(pick.getTaskId());
        return pickRequest;
    }

    private MoveRequest convert(Move move) {
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setDestLocation(move.getDestination());
        moveRequest.setToteId(move.getToteId());
        moveRequest.setTaskId(move.getTaskId());
        return moveRequest;
    }

    private DeliveryRequest convert(Delivery expose) {
        DeliveryRequest delivery = new DeliveryRequest();
        OrderId orderId = expose.getOrderId();
        Tote tote = toteService.findToteByToteId(expose.getToteId()).orElseThrow();
        List<Integer> ordinals = orderId!=null ?
                tote.getSlots().stream().filter(slot -> Objects.nonNull(slot.getDeliveryInventory())).filter(it -> orderId.equals(it.getDeliveryInventory().getOrderId())).map(Slot::getOrdinal).collect(toList())
                : tote.getSlots().stream().map(Slot::getOrdinal).collect(toList());
        delivery.setSlots(ordinals);
        delivery.setToteId(expose.getToteId());
        delivery.setTaskId(expose.getTaskId());
        return delivery;
    }
}
