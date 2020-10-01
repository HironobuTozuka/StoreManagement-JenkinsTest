package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

import java.util.List;

@Data
public class BatchToteActionRequest {

    private List<ToteId> toteIds;

}
