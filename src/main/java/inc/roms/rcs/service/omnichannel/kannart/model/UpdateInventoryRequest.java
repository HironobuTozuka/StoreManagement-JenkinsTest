package inc.roms.rcs.service.omnichannel.kannart.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.RcsOperationId;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;
import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATE_PATTERN;

@Data
public class UpdateInventoryRequest {

    public UpdateInventoryRequest() {
        rcsOperationId = RcsOperationId.generate();
    }

    @JsonProperty("sku")
    private SkuId sku;

    @JsonProperty("quantity")
    private Quantity quantity;

    private StoreId storeCode;

    @JsonInclude(NON_NULL)
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate deliveryDate;

    @JsonInclude(NON_NULL)
    private DeliveryTurn deliveryTurn;

    @JsonInclude(NON_NULL)
    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime updateDate;

    private InventoryUpdateType updateType;

    private RcsOperationId rcsOperationId;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SkuId sku;
        private Quantity quantity;
        private StoreId storeCode;
        private LocalDate deliveryDate;
        private DeliveryTurn deliveryTurn;
        private ZonedDateTime updateDate;
        private InventoryUpdateType updateType;

        public Builder sku(SkuId sku) {
            this.sku = sku;
            return this;
        }

        public Builder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder storeCode(StoreId storeCode) {
            this.storeCode = storeCode;
            return this;
        }

        public Builder deliveryDate(LocalDate deliveryDate) {
            this.deliveryDate = deliveryDate;
            return this;
        }

        public Builder deliveryTurn(DeliveryTurn deliveryTurn) {
            this.deliveryTurn = deliveryTurn;
            return this;
        }

        public Builder updateDate(ZonedDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder updateType(InventoryUpdateType updateType) {
            this.updateType = updateType;
            return this;
        }

        public UpdateInventoryRequest build() {
            UpdateInventoryRequest updateInventoryRequest = new UpdateInventoryRequest();
            updateInventoryRequest.setDeliveryDate(deliveryDate);
            updateInventoryRequest.setDeliveryTurn(deliveryTurn);
            updateInventoryRequest.setQuantity(quantity);
            updateInventoryRequest.setSku(sku);
            updateInventoryRequest.setUpdateDate(updateDate);
            updateInventoryRequest.setStoreCode(storeCode);
            updateInventoryRequest.setUpdateType(updateType);
            return updateInventoryRequest;
        }
    }
}
