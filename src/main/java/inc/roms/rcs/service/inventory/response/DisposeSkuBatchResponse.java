package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Data;

@Data
public class DisposeSkuBatchResponse {
    private ResponseCode responseCode;
    private DisposeSkuBatchResponseDetails details;
}
