package inc.roms.rcs.service.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
public class CreateOrderResponseDetails {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<RejectedSku> rejectedSkus;

    @JsonInclude(NON_NULL)
    private GateId gateId;

    @JsonInclude(NON_NULL)
    private LocalDateTime eta;

    @JsonInclude(NON_NULL)
    private OrderRejectReason orderRejectReason;

    @JsonInclude(NON_NULL)
    private SkuId missingSkuId;

    @JsonInclude(NON_NULL)
    private IssueId issueId;

    @Transient
    private CompletableFuture<TaskBundle> pickOrderFuture;

    public static Builder details() {
        return new Builder();
    }

    public static class Builder {
        private List<RejectedSku> rejectedSkus;
        private GateId gateId;
        private LocalDateTime eta;
        private OrderRejectReason orderRejectReason;
        private SkuId missingSkuId;
        private IssueId issueId;
        private CompletableFuture<TaskBundle> pickOrderFuture;

        public Builder pickFuture(CompletableFuture<TaskBundle> pickOrderFuture) {
            this.pickOrderFuture = pickOrderFuture;
            return this;
        }

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

        public Builder issueId(IssueId issueId) {
            this.issueId = issueId;
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
                createOrderResponseDetails.setIssueId(issueId);
            } else if(orderRejectReason != null && missingSkuId != null) {
                createOrderResponseDetails.setMissingSkuId(missingSkuId);
                createOrderResponseDetails.setOrderRejectReason(orderRejectReason);
                createOrderResponseDetails.setIssueId(issueId);
            } else if(orderRejectReason != null) {
                createOrderResponseDetails.setOrderRejectReason(orderRejectReason);
                createOrderResponseDetails.setIssueId(issueId);
            } else {
                createOrderResponseDetails.setPickOrderFuture(pickOrderFuture);
                createOrderResponseDetails.setEta(eta);
                createOrderResponseDetails.setGateId(gateId);
            }

            return createOrderResponseDetails;
        }

        public Builder unknownSku(SkuId externalId) {
            this.missingSkuId = externalId;
            return this;
        }
    }

}
