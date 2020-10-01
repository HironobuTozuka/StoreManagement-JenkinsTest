package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.vo.sku.SkuId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SkuBatchRepository extends CrudRepository<SkuBatch, Integer> {

    List<SkuBatch> findAllBySkuId(SkuId skuId);

    List<SkuBatch> findAllBySkuIdAndSellByDate(SkuId skuId, LocalDateTime sellByDate);

}
