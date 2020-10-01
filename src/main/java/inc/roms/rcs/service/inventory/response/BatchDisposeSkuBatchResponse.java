package inc.roms.rcs.service.inventory.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class BatchDisposeSkuBatchResponse {
    private final List<DisposeSkuBatchResponse> disposeSkuBatchRespons;
}
