package inc.roms.rcs.service.inventory.domain.repository;

import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.sku.SkuStatus;
import inc.roms.rcs.vo.sku.ExternalId;
import inc.roms.rcs.vo.sku.SkuId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends CrudRepository<Sku, SkuId> {

    Optional<Sku> findByExternalId(ExternalId externalId);

    @Override
    List<Sku> findAll();

    List<Sku> findAllByStatus(SkuStatus status);

    Optional<Sku> findBySkuIdAndStatus(SkuId skuId, SkuStatus status);

    Optional<Sku> findBySkuId(SkuId skuId);
}
