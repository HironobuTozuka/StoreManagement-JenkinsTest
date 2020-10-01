package inc.roms.rcs.service.order.config;

import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;

@Data
public class OrderGateConfig {

    private GateId gateId;
    private ZoneId zoneId;

}
