package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MoveRequest extends TaskBase {
    private ToteId toteId;
    private ZoneId destLocation;

}
