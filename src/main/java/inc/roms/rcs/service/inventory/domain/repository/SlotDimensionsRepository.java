package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.SlotDimensions;
import inc.roms.rcs.vo.tote.ToteType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotDimensionsRepository extends CrudRepository<SlotDimensions, ToteType> {
}
