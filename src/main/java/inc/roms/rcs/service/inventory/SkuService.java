package inc.roms.rcs.service.inventory;

import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.repository.SkuRepository;
import inc.roms.rcs.service.inventory.request.AddSkuRequest;
import inc.roms.rcs.service.inventory.response.SkuDetailsResponse;
import inc.roms.rcs.service.inventory.response.SkuListResponse;
import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.sku.ExternalId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.sku.SkuStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static inc.roms.rcs.vo.sku.SkuStatus.PREPARATION;
import static inc.roms.rcs.vo.sku.SkuStatus.READY;


//this should be turned into domain service, and all business logic should go through SkuManagementService
@Service
@RequiredArgsConstructor
@Slf4j
public class SkuService {

    private final SkuRepository skuRepository;
    private final BusinessExceptions businessExceptions;

    public Sku getReadySku(SkuId skuId) {
        Optional<Sku> maybeSku = skuRepository.findBySkuId(skuId);
        Sku sku = maybeSku.orElseThrow(() -> businessExceptions.skuNotFoundException(skuId));
        if(!READY.equals(sku.getStatus())) {
            throw businessExceptions.skuNotReadyException(skuId);
        }

        return sku;
    }

    public Optional<Sku> getSku(SkuId skuId) {
        return skuRepository.findBySkuId(skuId);
    }

    public Sku save(Sku sku) {
        return skuRepository.save(sku);
    }

    public Sku add(AddSkuRequest sku) {
        return save(convert(sku));
    }

    public Optional<Sku> findByExternalId(ExternalId externalId) {
        return skuRepository.findByExternalId(externalId);
    }

    private Sku convert(AddSkuRequest request) {
        Sku sku = new Sku();
        sku.setExternalId(request.getExternalId());
        sku.setMaxAcc(1d);
        sku.setSkuId(request.getSkuId());
        sku.setDimensions(new Dimensions(10, 10, 10));
        sku.setWeight(100d);
        sku.setName(request.getName());
        sku.setCategory(request.getCategory());
        sku.setImageUrl(request.getImageUrl());
        sku.setStatus(PREPARATION);
        sku.setDistributionType(request.getDistributionType());
        return sku;
    }

    public List<Sku> findAll() {
        return skuRepository.findAll();
    }

    public List<Sku> findAllByStatus(SkuStatus status) {
        return skuRepository.findAllByStatus(status);
    }

    /*
     * So called business methods.
     */
    public SkuListResponse list() {
        SkuListResponse.Builder builder = SkuListResponse.builder();
        builder.skus(findAll());
        return builder.build();
    }

    public SkuDetailsResponse details(SkuId skuId) {
        SkuDetailsResponse.Builder builder = SkuDetailsResponse.builder();
        builder.sku(getReadySku(skuId));
        return builder.build();
    }
}
