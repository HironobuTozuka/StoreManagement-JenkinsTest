package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

@Data
public class ToteResponse {
    private ToteId toteId;
    private ToteFunction toteFunction;

    public static ToteResponse from(Tote tote) {
        ToteResponse toteResponse = new ToteResponse();
        toteResponse.setToteId(tote.getToteId());
        toteResponse.setToteFunction(tote.getToteFunction());
        return toteResponse;
    }
}
