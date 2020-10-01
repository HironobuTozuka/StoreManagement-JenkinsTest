package inc.roms.rcs.service.location;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.location.model.Location;
import inc.roms.rcs.service.location.repository.LocationRepository;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    public void removeToteFromLocation(LocationId locationId) {
        locationRepository.removeToteFromLocation(locationId);
    }

    public void relocateTote(Tote totePrototype, LocationId locationId) {
        locationRepository.updateCurrentTote(locationId, totePrototype);
    }

    public Location getLocation(LocationId locationId) {
        return locationRepository.findByLocationId(locationId).orElse(null);
    }

}
