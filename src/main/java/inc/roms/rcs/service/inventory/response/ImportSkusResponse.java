package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportSkusResponse {

    private ResponseCode responseCode;
    private ImportSkuDetails importDetails;
}
