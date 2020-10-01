package inc.roms.rcs.service.inventory.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class BatchDisposeSkuBatchRequest {
    private final List<DisposeSkuBatchRequest> disposeSkuBatchRequests;
}
