package inc.roms.rcs.service.order.response;


import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Data;

@Data
public class DeliverOrderResponse {

    private ResponseCode responseCode;
    private DeliverOrderResponseDetails deliverOrderDetails;

}
