package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.sku.*;
import inc.roms.rcs.vo.common.Dimensions;
import lombok.Data;

@Data
public class SkuDetailsResponse {

    private SkuId skuId;

    private Name name;

    private SkuStatus status;

    private ImageUrl imageUrl;

    private ExternalId externalId;

    private Dimensions dimensions;

    private Category category;

    private Double weight;

    private Double maxAcc;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Sku sku;

        public SkuDetailsResponse build() {
            SkuDetailsResponse resonse = new SkuDetailsResponse();
            resonse.setDimensions(sku.getDimensions());
            resonse.setExternalId(sku.getExternalId());
            resonse.setImageUrl(sku.getImageUrl());
            resonse.setMaxAcc(sku.getMaxAcc());
            resonse.setName(sku.getName());
            resonse.setSkuId(sku.getSkuId());
            resonse.setCategory(sku.getCategory());
            resonse.setStatus(sku.getStatus());
            resonse.setWeight(sku.getWeight());
            return resonse;
        }

        public Builder sku(Sku sku) {
            this.sku = sku;
            return this;
        }
    }
}
