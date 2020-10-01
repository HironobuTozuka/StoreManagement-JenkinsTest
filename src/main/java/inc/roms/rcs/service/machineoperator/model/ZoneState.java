package inc.roms.rcs.service.machineoperator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.zones.ZoneFunction;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;

import java.util.List;

@Data
public class ZoneState {
        private final ZoneId zoneId;

        @JsonProperty("temp_regime")
        private final TemperatureRegime temperatureRegime;
        private final List<ZoneFunction> functions;
        private final Quantity availableLocations;
}
