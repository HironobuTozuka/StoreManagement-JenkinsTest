package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.ScheduledSupply;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.supply.SupplyId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledSupplyRepository extends CrudRepository<ScheduledSupply, Integer> {

    List<ScheduledSupply> findAllByDeliveryDate(LocalDate deliveryDate);

    List<ScheduledSupply> findAll();

    Optional<ScheduledSupply> findByDeliveryDateAndDeliveryTurnAndDistributionType(LocalDate deliveryDate, DeliveryTurn deliveryTurn, DistributionType type);

    ScheduledSupply findBySupplyId(SupplyId supplyId);
}
