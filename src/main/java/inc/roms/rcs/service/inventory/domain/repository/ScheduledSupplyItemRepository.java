package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.vo.supply.ScheduledSupplyItemId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledSupplyItemRepository extends CrudRepository<ScheduledSupplyItem, Integer> {
    ScheduledSupplyItem findByScheduledSupplyItemId(ScheduledSupplyItemId supplyItemId);
}
