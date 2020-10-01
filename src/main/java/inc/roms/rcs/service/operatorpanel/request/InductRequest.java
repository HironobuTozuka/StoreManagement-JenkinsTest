package inc.roms.rcs.service.operatorpanel.request;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

import java.util.List;

@Data
public class InductRequest {
    private List<StorageSlotModel> slots;
    private Integer id;
    private ToteId toteId;
}
