package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    List<Reservation> findAllByOrderLineIn(List<OrderLine> orderLines);

    @Query("select res from Reservation as res where res.deliveryInventory = :deliveryInventory")
    List<Reservation> getReservationsForDeliveryInventory(DeliveryInventory deliveryInventory);
}
