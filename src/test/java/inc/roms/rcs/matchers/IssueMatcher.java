package inc.roms.rcs.matchers;

import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.ToString;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@ToString
public class IssueMatcher implements ArgumentMatcher<Issue> {

    private IssueReason issueReason;
    private IssueAction issueAction;
    private ToteId toteId;
    private SkuId skuId;
    private OrderId orderId;
    private IssueId issueId;

    public static IssueMatcher matchesIssue() {
        return new IssueMatcher();
    }

    @Override
    public boolean matches(Issue argument) {
        return argument != null &&
                   (issueReason == null || Objects.equals(argument.getReason(), issueReason))
                && (issueAction == null || Objects.equals(argument.getIssueAction(), issueAction))
                && (toteId == null || Objects.equals(argument.getToteId(), toteId))
                && (skuId == null || Objects.equals(argument.getSkuId(), skuId))
                && (orderId == null || Objects.equals(argument.getOrderId(), orderId))
                && (issueId == null || Objects.equals(argument.getIssueId(), issueId))
                ;
    }

    public IssueMatcher withReason(IssueReason issueReason) {
        this.issueReason = issueReason;
        return this;
    }

    public IssueMatcher withAction(IssueAction issueAction) {
        this.issueAction = issueAction;
        return this;
    }

    public IssueMatcher withToteId(ToteId toteId) {
        this.toteId = toteId;
        return this;
    }

    public IssueMatcher withSkuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public IssueMatcher withOrderId(OrderId orderId) {
        this.orderId = orderId;
        return this;
    }

    public IssueMatcher withIssueId(IssueId issueId) {
        this.issueId = issueId;
        return this;
    }
}
