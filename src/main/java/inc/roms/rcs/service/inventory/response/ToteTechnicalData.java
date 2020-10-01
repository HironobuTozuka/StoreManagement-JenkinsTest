package inc.roms.rcs.service.inventory.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

@Data
public class ToteTechnicalData {

    @JsonUnwrapped
    private final ToteId toteId;
    private final Double maxAcc;
    private final Double weight;

    public ToteTechnicalData(ToteId toteId, Double maxAcc, Double weight) {
        this.toteId = toteId;
        this.maxAcc = maxAcc;
        this.weight = weight;
    }

    public static Builder builder(ToteId toteId) {
        return new Builder(toteId);
    }

    public static class Builder {
        private ToteId toteId;
        private boolean defaultMaxAcc = true;
        private Double maxAcc = 2D;
        private Double weight = 0D;

        public Builder(ToteId toteId) {
            this.toteId = toteId;
        }

        public Builder sku(Sku sku, Quantity quantity) {
            if(defaultMaxAcc) {
                maxAcc = sku.getMaxAcc();
                defaultMaxAcc = false;
            }

            maxAcc = Math.min(sku.getMaxAcc(), maxAcc);
            weight += sku.getWeight() * quantity.getQuantity();

            return this;
        }

        public ToteTechnicalData build() {
            return new ToteTechnicalData(toteId, maxAcc, weight);
        }
    }
}
