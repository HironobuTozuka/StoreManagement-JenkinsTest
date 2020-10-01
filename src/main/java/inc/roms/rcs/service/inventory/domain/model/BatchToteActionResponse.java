package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BatchToteActionResponse {

    private final ResponseCode responseCode;
    private final ToteBatchResponseDetails responseDetails;

}
