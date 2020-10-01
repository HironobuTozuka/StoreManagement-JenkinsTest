package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleSupplyResponse {
    private ResponseCode responseCode;
    private ScheduleSupplyResponseDetails details;
}
