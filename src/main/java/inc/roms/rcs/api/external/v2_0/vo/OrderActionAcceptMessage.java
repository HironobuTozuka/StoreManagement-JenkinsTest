package inc.roms.rcs.api.external.v2_0.vo;

import inc.roms.rcs.vo.order.OrderId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderActionAcceptMessage {

    private final OrderActionRejectReason errorCode;
    private final List<OrderId> missingOrderNos;
}
