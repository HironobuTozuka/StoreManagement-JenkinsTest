package inc.roms.rcs.service.order.config;

import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static inc.roms.rcs.vo.location.GateId.LOADING_GATE;

@ConfigurationProperties(prefix = "inc.roms.gates")
@Component
@Data
public class GateProperties {

    private ZoneId loadingGateZone;
    private List<OrderGateConfig> ordergates = new ArrayList<>();

    public Optional<ZoneId> gateZone(GateId gateId) {
        if(gateId.equals(LOADING_GATE)) return Optional.of(loadingGateZone);
        return getOrdergates().stream().filter(it -> it.getGateId().equals(gateId)).map(OrderGateConfig::getZoneId).findFirst();
    }

    public List<GateId> availableOrderGateIds() {
        return ordergates.stream().map(OrderGateConfig::getGateId).collect(Collectors.toList());
    }
}
