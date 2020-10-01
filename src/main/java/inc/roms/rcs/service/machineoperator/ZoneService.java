package inc.roms.rcs.service.machineoperator;

import inc.roms.rcs.service.machineoperator.model.ZoneState;
import inc.roms.rcs.service.machineoperator.model.ZonesStatus;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.zones.ZoneFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final MachineOperatorClient machineOperatorClient;

    public ZoneState getZone(ZoneFunction zoneFuntion, TemperatureRegime temperatureRegime) {
        ZonesStatus zones = machineOperatorClient.getZones();

        return zones.getZones().stream()
                .filter(it -> it.getTemperatureRegime().equals(temperatureRegime))
                .filter(it -> it.getFunctions().contains(zoneFuntion))
                .findFirst().orElseThrow();
    }

    public List<ZoneState> getZones(ZoneFunction zoneFunction) {
        ZonesStatus zones = machineOperatorClient.getZones();
        return zones.getZones().stream().filter(it -> it.getFunctions().contains(zoneFunction)).collect(toList());
    }

    public ZoneState getZone(ZoneFunction zoneFunction) {
        ZonesStatus zones = machineOperatorClient.getZones();

        return zones.getZones().stream()
                .filter(it -> it.getFunctions().contains(zoneFunction))
                .findFirst().orElseThrow();
    }

}
