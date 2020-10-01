package inc.roms.rcs.service.location.repository;

import inc.roms.rcs.service.location.model.Location;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

    Optional<Location> findByLocationId(LocationId location);

    @Modifying
    @Query("update Location l set l.currentTote = :tote where l.locationId = :locationId")
    void updateCurrentTote(LocationId locationId, Tote tote);

    @Modifying
    @Query("update Location l set l.currentTote = null where l.locationId = :locationId")
    void removeToteFromLocation(LocationId locationId);

}
