package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.tote.ToteType;
import inc.roms.rcs.vo.zones.ZoneId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryToteRepository implements ToteRepository {

    private final Map<Integer, Tote> totes = new HashMap<>();

    @Override
    public Tote getToteWithStorageReservation(Reservation reservation) {
        return null;
    }

    @Override
    public List<Tote> getDeliveryTotesByOrderId(OrderId orderId) {
        return null;
    }

    @Override
    public Tote getToteWithDeliveryInventory(DeliveryInventory deliveryInventory) {
        return null;
    }

    @Override
    public List<Tote> getBySkuIdAndToteFunction(SkuId skuId, ToteFunction toteFunction) {
        return null;
    }

    @Override
    public Integer countByToteFunction(ToteFunction toteFunction) {
        return null;
    }

    @Override
    public List<Tote> findAllByZoneId(ZoneId zoneId) {
        return null;
    }

    @Override
    public List<Tote> findAllByStorageReservations(List<Reservation> orderReservations) {
        return null;
    }

    @Override
    public Optional<Tote> findByToteId(ToteId id) {
        return Optional.empty();
    }

    @Override
    public <S extends Tote> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Tote> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Tote> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<Tote> findAll() {
        return null;
    }

    @Override
    public List<Tote> findAllByToteStatusAndToteFunction(ToteStatus toteStatus, ToteFunction toteFunction) {
        return null;
    }

    @Override
    public List<Tote> findAllByToteTypeAndToteStatusAndToteFunction(ToteType toteType, ToteStatus status, ToteFunction toteFunction) {
        return null;
    }

    @Override
    public List<Tote> getTotesWithAvailableSkuAndToteFunction(SkuId skuId, ToteFunction toteFunction) {
        return null;
    }

    @Override
    public Iterable<Tote> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(Tote entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Tote> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Optional<Tote> findOne(Specification<Tote> spec) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Tote> findAll(Specification<Tote> spec) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Tote> findAll(Specification<Tote> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Tote> findAll(Specification<Tote> spec, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count(Specification<Tote> spec) {
        // TODO Auto-generated method stub
        return 0;
    }

}
