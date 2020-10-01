package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.inventory.domain.repository.SkuBatchRepository;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkuBatchService {

    private final SkuBatchRepository skuBatchRepository;

    public List<SkuBatch> findAllBySkuId(SkuId skuId) {
        return skuBatchRepository.findAllBySkuId(skuId);
    }

    public List<SkuBatch> findAllBySkuIdAndSellByDate(SkuId skuId, LocalDateTime sellByDate) {
        return skuBatchRepository.findAllBySkuIdAndSellByDate(skuId, sellByDate);
    }

    public void save(SkuBatch skuBatch) {
        skuBatchRepository.save(skuBatch);
    }
}
