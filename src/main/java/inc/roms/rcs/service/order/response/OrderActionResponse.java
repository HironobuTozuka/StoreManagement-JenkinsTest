package inc.roms.rcs.service.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderActionResponse {

    private ResponseCode responseCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OrderActionResponseDetails details;

}
