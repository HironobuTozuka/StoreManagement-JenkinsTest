package inc.roms.rcs.service.issue.request;

import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.issue.Notes;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.SupplyId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
public class CreateIssueRequest {

    private final LocalDateTime issueDeadline;

    private final IssueAction issueAction;

    private final IssueReason reason;

    private final ToteId toteId;

    private final SkuId skuId;

    private final SupplyId supplyId;

    private final Notes notes;

    private final TemperatureRegime temperatureRegime;

    private final OrderId orderId;

    private final ZoneId zoneId;

    public static Builder issue() {
        return new Builder();
    }

    public static class Builder {

        private LocalDateTime issueDeadline;

        private IssueAction issueAction;

        private IssueReason reason;

        private ToteId toteId;

        private SkuId skuId;

        private SupplyId supplyId;

        private Notes notes;

        private TemperatureRegime temperatureRegime;

        private OrderId orderId;

        private ZoneId zoneId;

        public Builder deadlineTomorrow(Clock clock) {
            this.issueDeadline = LocalDateTime.now(clock).plusDays(1);
            return this;
        }

        public Builder issueDeadline(LocalDateTime issueDeadline) {
            this.issueDeadline = issueDeadline;
            return this;
        }

        public Builder issueAction(IssueAction issueAction) {
            this.issueAction = issueAction;
            return this;
        }

        public Builder reason(IssueReason reason) {
            this.reason = reason;
            return this;
        }

        public Builder toteId(ToteId toteId) {
            this.toteId = toteId;
            return this;
        }

        public Builder skuId(SkuId skuId) {
            this.skuId = skuId;
            return this;
        }

        public Builder zoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Builder supplyId(SupplyId supplyId) {
            this.supplyId = supplyId;
            return this;
        }

        public Builder notes(Notes notes) {
            this.notes = notes;
            return this;
        }

        public Builder temperatureRegime(TemperatureRegime temperatureRegime) {
            this.temperatureRegime = temperatureRegime;
            return this;
        }

        public CreateIssueRequest build() {
            return new CreateIssueRequest(
                    issueDeadline,
                    issueAction,
                    reason,
                    toteId,
                    skuId,
                    supplyId,
                    notes,
                    temperatureRegime,
                    orderId,
                    zoneId);
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }
    }

}
