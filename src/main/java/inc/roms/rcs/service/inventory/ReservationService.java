package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.repository.ReservationRepository;
import inc.roms.rcs.service.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> getReservationsFor(Order order) {
        return reservationRepository.findAllByOrderLineIn(order.getOrderLines());
    }

    public Reservation getById(Integer id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsForDeliveryInventory(DeliveryInventory deliveryInventory) {
        return reservationRepository.getReservationsForDeliveryInventory(deliveryInventory);
    }

    public void deleteReservations(List<Reservation> reservations) {
        reservationRepository.deleteAll(reservations);
    }
}
