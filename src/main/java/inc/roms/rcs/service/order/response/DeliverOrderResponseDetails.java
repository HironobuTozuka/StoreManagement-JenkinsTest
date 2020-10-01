package inc.roms.rcs.service.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class DeliverOrderResponseDetails {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeliverOrderRejectReason orderRejectReason;


}