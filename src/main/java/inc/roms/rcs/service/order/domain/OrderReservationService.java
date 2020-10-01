package inc.roms.rcs.service.order.domain;

import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.NotEnoughSkuToFulfillOrderException;
import inc.roms.rcs.service.order.exception.NotEnoughSkuToFulfillOrderLineException;
import inc.roms.rcs.service.order.exception.OrderNotFoundException;
import inc.roms.rcs.service.order.response.RejectedSku;
import inc.roms.rcs.service.order.response.Reason;
import inc.roms.rcs.service.cubing.CubingService;
import inc.roms.rcs.service.inventory.ReservationService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static inc.roms.rcs.vo.order.OrderStatus.READY_TO_BE_PICKED;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class  OrderReservationService {

    private final ToteService toteService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final CubingService cubingService;
    private final BusinessExceptions businessExceptions;

    public void makeReservationsFor(Order order) {
        List<RejectedSku> rejectedSkus = new ArrayList<>();
        order.getOrderLines().forEach(it -> {
            try {
                this.makeReservations(it);
            } catch (NotEnoughSkuToFulfillOrderLineException notEnoughSku) {
                rejectedSkus.add(new RejectedSku(notEnoughSku.getOrderLine().getSkuId(), Reason.NOT_ENOUGH, notEnoughSku.getMissingQuantity()));
            }
        });
        if (!rejectedSkus.isEmpty()) throw new NotEnoughSkuToFulfillOrderException(rejectedSkus);
    }

    public void makeReservations(OrderLine orderLine) {
        Quantity quantityToBeReserved = orderLine.getQuantity();
        List<Tote> totes = toteService.getStorageToteWithAvailableSku(orderLine.getSkuId());
        Iterator<Tote> toteIterator = totes.iterator();
        while (toteIterator.hasNext() && quantityToBeReserved.gt(0)) {
            Tote tote = toteIterator.next();
            List<Slot> slots = tote.getAllSlots()
                    .stream()
                    .filter(it -> it.getStorageInventory() != null)
                    .filter(it -> orderLine.getSkuId().equals(it.getStorageInventory().getSkuBatch().getSkuId()))
                    .filter(it -> it.getStorageInventory().getAvailable().gt(0))
                    .collect(Collectors.toList());

            for (Slot slot : slots) {
                if (quantityToBeReserved.gt(0)) {
                    Reservation reservation = new Reservation();
                    reservation.setQuantity(Quantity.min(quantityToBeReserved, slot.getStorageInventory().getAvailable()));
                    reservation.setOrderLine(orderLine);
                    reservation.setSkuId(orderLine.getSkuId());
                    slot.getStorageInventory().addReservation(reservation);
                    quantityToBeReserved = quantityToBeReserved.minus(reservation.getQuantity());
                }
            }
            toteService.updateTote(tote);
        }

        if (!toteIterator.hasNext() && quantityToBeReserved.gt(0))
            throw new NotEnoughSkuToFulfillOrderLineException(orderLine, quantityToBeReserved);
    }

    public void reserveDeliverySlots(Order order) {
        List<Reservation> reservations = getReservationsFor(order);

        reservations.forEach(it -> {
            if (!boundToProperDeliveryTote(it)) {
                List<Tote> deliveryTotes = toteService.getDeliveryTotesFor(order.getOrderId())
                        .stream().filter(tote -> !ToteStatus.IN_ERROR.equals(tote.getToteStatus()))
                        .collect(toList());

                boolean foundMatchingTote = deliveryTotes.stream()
                        .anyMatch(deliveryTote -> this.bindToTote(it, deliveryTote, order.getOrderId()));
                if (!foundMatchingTote)
                    this.bindToNewDeliveryTote(it, order.getOrderId());
            }
        });

        order.setOrderStatus(READY_TO_BE_PICKED);
        orderService.save(order);
    }

    public void removeStorageReservations(OrderId orderId) {
        Order order = orderService.getByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        List<Reservation> orderReservations = getReservationsFor(order);
        orderReservations.forEach(toteService::cleanStorageReservation);
    }

    private boolean boundToProperDeliveryTote(Reservation it) {
        log.debug("Checking if reservation with id {} is already bound to delivery tote", it.getId());
        Tote potentialTote = toteService.getToteWithDeliveryInventory(it.getDeliveryInventory());
        if(potentialTote == null) {
            log.debug("Checking if reservation with id {} is already bound to delivery tote: not bound, as tote not found in db!", it.getId());
            return false;
        }

        if(ToteStatus.IN_ERROR.equals(potentialTote.getToteStatus())) {
            log.debug("Checking if reservation with id {} is already bound to delivery tote: not bound, as tote {} is in ERROR state!", it.getId(), potentialTote.getToteId());
            return false;
        }

        log.debug("Checking if reservation with id {} is already bound to delivery tote: bound to tote {}", it.getId(), potentialTote.getToteId());
        return true;
    }

    private boolean bindToTote(Reservation reservation, Tote deliveryTote, OrderId orderId) {
        log.debug("Trying to binding order {} reservation with id {} to existing tote: {}", orderId, reservation.getId(), deliveryTote.getToteId());
        List<Slot> possibleSlots = deliveryTote.getAllSlots()
                .stream()
                .filter(it -> it.isEmpty() || (it.getDeliveryInventory() != null && orderId.equals(it.getDeliveryInventory().getOrderId())))
                .collect(toList());

        Optional<Slot> first = possibleSlots
                .stream()
                .filter(slot -> cubingService.canReservationFitIntoDeliverySlot(reservation, slot.getDeliveryInventory()))
                .findFirst();

        if (first.isEmpty()) {
            log.debug("Binding order {} reservation with id {} to existing tote: {} failed as no matching slot was found", orderId, reservation.getId(), deliveryTote.getToteId());
            return false;
        }

        Slot slot = first.get();

        if (slot.getDeliveryInventory() == null) {
            slot.setDeliveryInventory(new DeliveryInventory());
            slot.getDeliveryInventory().setOrderId(orderId);
        }

        List<Reservation> reservations = reservationService.getReservationsForDeliveryInventory(slot.getDeliveryInventory());
        reservations.add(reservation);
        slot.getDeliveryInventory().setReservations(reservations);
        deliveryTote.addSlot(slot);
        toteService.updateTote(deliveryTote);
        log.debug("Binding order {} reservation with id {} to existing tote: {} successful", orderId, reservation.getId(), deliveryTote.getToteId());
        return true;
    }

    private void bindToNewDeliveryTote(Reservation reservation, OrderId orderId) {
        log.debug("Binding order {} reservation with id {} to new tote", orderId, reservation.getId());
        Tote deliveryTote = toteService
                .getNextAvailableDeliveryTote()
                .orElseThrow(NoEmptyTotesException::new);

        log.debug("Binding order {} reservation with id {} to new tote. Found tote: {}", orderId, reservation.getId(), deliveryTote.getToteId());

        Slot deliverySlot = deliveryTote.getAllSlots().stream().filter(Slot::isEmpty).findFirst().map(it -> {
            it.setStorageInventory(null);
            it.setDeliveryInventory(new DeliveryInventory());
            it.getDeliveryInventory().setOrderId(orderId);
            it.getDeliveryInventory().addReservation(reservation);
            return it;
        }).orElseThrow(businessExceptions::noEmptyTotesException);


        deliveryTote.addSlot(deliverySlot);
        deliveryTote.setToteStatus(ToteStatus.RESERVED);
        toteService.updateTote(deliveryTote);
        log.debug("Binding order {} reservation with id {} to new tote. Reservation bound to tote {}", orderId, reservation.getId(), deliveryTote.getToteId());
    }

    public List<Reservation> getReservationsFor(Order order) {
        return reservationService.getReservationsFor(order);
    }
}
