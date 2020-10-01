package inc.roms.rcs.service.inventory.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.service.inventory.request.ToteListRequest;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.tote.ToteType;

import inc.roms.rcs.vo.zones.ZoneId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import static inc.roms.rcs.service.inventory.domain.model.SkuBatchState.DISPOSED;

@Repository
public interface ToteRepository extends CrudRepository<Tote, Integer>, JpaSpecificationExecutor<Tote> {

    Optional<Tote> findByToteId(ToteId id);

    @Override
    List<Tote> findAll();

    List<Tote> findAllByToteStatusAndToteFunction(ToteStatus toteStatus, ToteFunction toteFunction);

    List<Tote> findAllByToteTypeAndToteStatusAndToteFunction(ToteType toteType, ToteStatus status, ToteFunction toteFunction);

    @Query("select t from Tote as t inner join t.slots as slot inner join slot.storageInventory inv inner join inv.skuBatch skuBatch where skuBatch.skuId = :skuId AND inv.available.quantity > 0 AND t.toteFunction = :toteFunction")
    List<Tote> getTotesWithAvailableSkuAndToteFunction(SkuId skuId, ToteFunction toteFunction);

    @Query("select t from Tote as t inner join t.slots as slot inner join slot.storageInventory inv inner join inv.reservations as reservation where reservation = :reservation")
    Tote getToteWithStorageReservation(Reservation reservation);

    @Query("select t from Tote as t inner join fetch t.slots as slot inner join slot.deliveryInventory inv inner join inv.reservations where inv.orderId = :orderId")
    List<Tote> getDeliveryTotesByOrderId(OrderId orderId);

    @Query("select t from Tote as t inner join t.slots as slot where slot.deliveryInventory = :deliveryInventory")
    Tote getToteWithDeliveryInventory(DeliveryInventory deliveryInventory);

    @Query("select t from Tote as t inner join t.slots as slot inner join slot.storageInventory inv inner join inv.skuBatch as skuBatch where skuBatch.skuId = :skuId and t.toteFunction = :toteFunction")
    List<Tote> getBySkuIdAndToteFunction(SkuId skuId, ToteFunction toteFunction);

    Integer countByToteFunction(ToteFunction toteFunction);

    List<Tote> findAllByZoneId(ZoneId zoneId);

    static Specification<Tote> buildSpec(ToteListRequest r) {
        return (tote, query, criteriaBuilder) -> {

            List<Predicate> restrictions = new ArrayList<>();
            if (r.getHeight() != null) {
                restrictions.add(criteriaBuilder.equal(tote.get("toteType").get("toteHeight"), r.getHeight()));
            }
            if (r.getPartitioning() != null) {
                restrictions.add(
                        criteriaBuilder.equal(tote.get("toteType").get("totePartitioning"), r.getPartitioning()));
            }
            if (r.getStatus() != null) {
                restrictions.add(criteriaBuilder.equal(tote.get("toteStatus"), r.getStatus()));
            }
            if (r.getToteId() != null) {
                restrictions.add(criteriaBuilder.equal(tote.get("toteId"), r.getToteId()));
            }
            if (r.getOrderId() != null) {
                restrictions
                        .add(criteriaBuilder.equal(tote.join("slots").get("storageInventory").get("available"), 100));
            }
            if (r.getSku() != null) {
                restrictions
                        .add(criteriaBuilder.equal(tote.join("slots").get("storageInventory").get("skuBatch").get("skuId"), r.getSku()));
            }
            if (r.getHasDeliveryReservations() != null) {
                restrictions
                        .add(criteriaBuilder.isNotEmpty(tote.join("slots").get("deliveryInventory").get("reservations")));
            }
            if(r.getOnlyDisposedStock() != null && r.getOnlyDisposedStock()) {
                restrictions
                        .add(criteriaBuilder.equal(tote.join("slots").get("storageInventory").get("skuBatch").get("state"), DISPOSED));
            }
            return criteriaBuilder.and(restrictions.toArray(new Predicate[restrictions.size()]));
        };
    }

    @Query("select t from Tote as t inner join t.slots as slot inner join slot.storageInventory inv inner join inv.reservations as reservation where reservation in :orderReservations")
    List<Tote> findAllByStorageReservations(List<Reservation> orderReservations);
}
