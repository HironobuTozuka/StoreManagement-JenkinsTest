package inc.roms.rcs.service.machineoperator;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.machineoperator.model.*;
import inc.roms.rcs.service.order.config.GateProperties;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.exception.UnknownGateException;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.domain.model.Delivery;
import inc.roms.rcs.service.task.domain.model.Move;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneFunction;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static inc.roms.rcs.service.task.domain.model.TaskBundle.deliveryBundle;
import static inc.roms.rcs.service.task.domain.model.TaskBundle.moveBundle;
import static inc.roms.rcs.service.task.domain.model.TaskBundleStatus.SENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class MachineOperatorService {

    private final TaskBundleService taskBundleService;
    private final ToteService toteService;
    private final GateProperties gateProperties;

    private final MachineOperatorClient machineOperatorClient;

    private final ExecuteTaskBundleFactory executeTaskBundleFactory;

    private final ZoneService zoneService;

    public void openLoadingGate() {
        log.info("Open loading gate");
        machineOperatorClient.openGate(new GateRequest(loadingZone(), TransactionId.generate()));
    }

    private ZoneId loadingZone() {
        return zoneService.getZone(ZoneFunction.LOADING_GATE).getZoneId();
    }

    public void closeGate(GateId gateId, TransactionId transactionId) {
        log.debug("Closing gate: {}", gateId);
        machineOperatorClient.closeGate(new GateRequest(toGateZone(gateId), transactionId));
    }

    public void closeLoadingGate(TransactionId transactionId) {
        log.debug("Closing gate: loading gate");
        machineOperatorClient.closeGate(new GateRequest(gateProperties.getLoadingGateZone(), transactionId));
    }

    public void moveTote(ToteId toteId, ZoneFunction destFunction) {
        log.info("Tote: {}, moved to zone with function: {}", toteId, destFunction);
        TaskBundle taskBundle = moveBundle();
        ZoneState zone = zoneService.getZone(destFunction);
        log.info("Tote: {}, moved to zone with id: {}", toteId, zone.getZoneId());
        taskBundle.add(new Move(toteId, zone.getZoneId()));
        taskBundleService.save(taskBundle);
        sendBundle(taskBundle);
    }

    public void moveTote(ToteId toteId, ZoneId destinationId) {
        log.info("Tote: {}, moved to {}", toteId, destinationId);
        TaskBundle taskBundle = moveBundle();
        taskBundle.add(new Move(toteId, destinationId));
        taskBundleService.save(taskBundle);
        sendBundle(taskBundle);
    }

    public void deliverTote(ToteId toteId, ZoneId destinationId) {
        log.info("Tote: {}, moved to {}", toteId, destinationId);
        TaskBundle taskBundle = deliveryBundle();
        taskBundle.add(new Move(toteId, destinationId));
        taskBundle.add(new Delivery(toteId));
        taskBundleService.save(taskBundle);
        sendBundle(taskBundle);
    }

    public void sendToStaging(Order order) {
        log.info("Sending  order {} to staging", order.getOrderId());
        TaskBundle moveToStaging = moveBundle();
        List<Tote> totes = toteService.getDeliveryTotesFor(order.getOrderId());
        Set<Tote> deliveryTotesFor = new HashSet<>(totes);
        deliveryTotesFor.stream().map(it -> new Move(it.getToteId(), zoneService.getZone(ZoneFunction.STAGING).getZoneId())).forEach(moveToStaging::add);
        taskBundleService.save(moveToStaging);
        sendBundle(moveToStaging);
    }

    public void sendToGate(Order order) {
        log.info("Sending  order {} to pick gate", order.getOrderId());
        TaskBundle deliveryBundle = deliveryBundle(order.getOrderId());
        List<Tote> totes = toteService.getDeliveryTotesFor(order.getOrderId());
        Set<Tote> deliveryTotesFor = totes.stream().filter(it -> !cleanEmpty(it)).collect(Collectors.toSet());
        deliveryTotesFor.stream().map(it -> new Move(it.getToteId(), toGateZone(order))).forEach(deliveryBundle::add);
        deliveryTotesFor.stream().map(it -> new Delivery(it.getToteId(), order.getOrderId())).forEach(deliveryBundle::add);
        taskBundleService.save(deliveryBundle);
        sendBundle(deliveryBundle);
    }

    private boolean cleanEmpty(Tote tote) {
        if(tote.getNotEmptySlotsCount() == 0) {
            toteService.clean(tote.getToteId());
            return true;
        }
        return false;
    }

    private ZoneId toGateZone(Order order) {
        return gateProperties.gateZone(order.getGate()).orElseThrow(() -> new UnknownGateException(order.getOrderId(), order.getGate(), gateProperties));
    }

    private ZoneId toGateZone(GateId gateId) {
        return gateProperties.gateZone(gateId).orElseThrow(() -> new UnknownGateException(null, gateId, gateProperties));
    }

    public void sendBundle(TaskBundle taskBundle) {
        ExecuteTaskBundleRequest executeTaskBundleRequest = executeTaskBundleFactory.convertToRequest(taskBundle);
        machineOperatorClient.executeBundle(executeTaskBundleRequest);
        taskBundle.setStatus(SENT);
    }

    public void turnOnLights(LedsRequest ledsRequest) {
        machineOperatorClient.turnLightsOn(ledsRequest);
    }

    public void turnLightsOff() {
        machineOperatorClient.turnLightsOn(new LedsRequest());
    }

    public void updateBundle(TaskBundle taskBundle) {
        ExecuteTaskBundleRequest executeTaskBundleRequest = executeTaskBundleFactory.convertToRequest(taskBundle);
        machineOperatorClient.updateBundle(executeTaskBundleRequest);
        taskBundle.setStatus(SENT);
    }

    @Async
    public void cancelBundle(TaskBundle taskBundle) {
        machineOperatorClient.cancel(new CancelTaskBundleRequest(taskBundle.getTaskBundleId()));
    }

    public void prepareNewToteForDelivery() {
        if(toteService.isPlaceLocationOccupied()) {
            log.debug("There is already a tote on place location!");
            return;
        }

        log.debug("No tote on place location, sending one");
        Optional<Tote> deliveryTote = toteService.findAvailableDeliveryTote();

        deliveryTote.ifPresent(dt -> {
            log.info("Sending {} to place location", dt.getToteId());
            ZoneState placeZone = zoneService.getZone(ZoneFunction.PLACE);
            if(placeZone.getAvailableLocations().gt(0)) {
                moveTote(dt.getToteId(), placeZone.getZoneId());
                dt.setZoneId(placeZone.getZoneId());
                toteService.updateTote(dt);
            }
        });
    }

}
