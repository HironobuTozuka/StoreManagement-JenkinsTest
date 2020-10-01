package inc.roms.rcs.api.external.v1_0.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.service.order.response.RejectedSku;
import inc.roms.rcs.service.order.response.OrderRejectReason;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.sku.ExternalId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderResponseDetails {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RejectedSku> rejectedSkus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gateId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime eta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderRejectReason orderRejectReason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExternalId missingExternalSkuId;

    public static CreateOrderResponseDetails.Builder details() {
        return new Builder();
    }

    public static class Builder {
        private List<RejectedSku> rejectedSkus;
        private GateId gateId;
        private LocalDateTime eta;
        private OrderRejectReason orderRejectReason;
        private ExternalId missingExternalSkuId;

        public Builder rejectedSkus(List<RejectedSku> rejectedSkus) {
            this.rejectedSkus = rejectedSkus;
            return this;
        }

        public Builder gateId(GateId gateId) {
            this.gateId = gateId;
            return this;
        }

        public Builder eta(LocalDateTime eta) {
            this.eta = eta;
            return this;
        }

        public Builder rejectReason(OrderRejectReason orderRejectReason) {
            this.orderRejectReason = orderRejectReason;
            return this;
        }

        public CreateOrderResponseDetails build() {
            CreateOrderResponseDetails createOrderResponseDetails = new CreateOrderResponseDetails();
            if(rejectedSkus != null && !rejectedSkus.isEmpty()) {
                createOrderResponseDetails.setRejectedSkus(rejectedSkus);
                createOrderResponseDetails.setOrderRejectReason(orderRejectReason);
            } else if(orderRejectReason != null && missingExternalSkuId != null) {
                createOrderResponseDetails.setMissingExternalSkuId(missingExternalSkuId);
                createOrderResponseDetails.setOrderRejectReason(orderRejectReason);
            } else if(orderRejectReason != null) {
                createOrderResponseDetails.setOrderRejectReason(orderRejectReason);
            } else {
                createOrderResponseDetails.setEta(eta);
                createOrderResponseDetails.setGateId(gateId);
            }

            return createOrderResponseDetails;
        }

        public Builder unknownSku(ExternalId externalId) {
            this.missingExternalSkuId = externalId;
            return this;
        }
    }

}
