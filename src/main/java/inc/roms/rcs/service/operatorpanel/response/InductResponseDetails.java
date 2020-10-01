package inc.roms.rcs.service.operatorpanel.response;

import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

@Data
public class InductResponseDetails {

    private final ToteId toteId;
    private final ToteFunction toteFunction;
}
