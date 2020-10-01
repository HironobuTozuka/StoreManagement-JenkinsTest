package inc.roms.rcs.api.external.v2_0.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
public class AcceptMessage {

    @JsonInclude(NON_NULL)
    private IssueReason errorCode;

    @JsonInclude(NON_EMPTY)
    private List<SkuId> skus;

    @JsonInclude(NON_NULL)
    private SkuId sku;

    @JsonInclude(NON_NULL)
    private OrderId orderNo;

    @JsonInclude(NON_NULL)
    private IssueId issueId;

    @JsonIgnore
    public boolean isEmpty() {
        return errorCode == null && skus == null && sku == null && orderNo == null;
    }
}
