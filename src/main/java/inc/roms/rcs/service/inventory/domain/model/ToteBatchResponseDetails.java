package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ToteBatchResponseDetails {

    private final List<ToteId> success;
    private final List<ToteId> failures;

}
