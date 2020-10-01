package inc.roms.rcs.service.inventory.request;

import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class DisposeSkuBatchRequest {
    private final SkuId skuId;
    private final LocalDateTime sellByDate;

}
