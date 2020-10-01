package inc.roms.rcs.service.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "responseDetails")
@Data
public class OrderActionResponseDetails {

    OrderActionResponseReason reason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    OrderId orderId;

}
