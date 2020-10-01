package inc.roms.rcs.service.inventory.response;

import java.util.ArrayList;
import java.util.List;

import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import lombok.Getter;

@Getter
public class SkuListResponse {

    private List<SkuDetailsResponse> skus;

    private ListResponseMetaDetails meta;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Sku> skus;

        public Builder skus(List<Sku> skus) {
            this.skus = skus;
            return this;
        }

        public SkuListResponse build() {

            SkuDetailsResponse.Builder skuDetailsBuilder = SkuDetailsResponse.builder();
            SkuListResponse response = new SkuListResponse();
            response.skus = new ArrayList<>();

            for (Sku sku : skus) {
                skuDetailsBuilder.sku(sku);
                response.skus.add(skuDetailsBuilder.build());
            }

            response.meta = new ListResponseMetaDetails(response.skus.size());
            // this is to show that the meta is required
            // resonse.meta.put("result_size", _result.size());
            // resonse.meta.put("search_param_1", value_1);
            // resonse.meta.put("search_param_2", value_2);
            // resonse.meta.put("page_size", value_2);
            // musi byc w przyszlosci paginacja bo moze byc kilka tys sku.
            return response;

        }

    }

}
