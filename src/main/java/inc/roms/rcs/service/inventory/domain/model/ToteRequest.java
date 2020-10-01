package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

@Data
public class ToteRequest {

    private ToteId toteId;

    public static ToteRequest from(ToteId toteId) {
        ToteRequest toteRequest = new ToteRequest();
        toteRequest.setToteId(toteId);
        return toteRequest;
    }

}
