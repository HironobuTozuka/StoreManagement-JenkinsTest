package inc.roms.rcs.service.order.response;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Data;

@Data
public class CreateOrderResponse {

    private ResponseCode responseCode;
    private CreateOrderResponseDetails responseDetails;

    public static Builder createOrderResponse() {
        return new Builder();
    }

    public static class Builder {
        private CreateOrderResponseDetails.Builder responseDetails;

        public Builder responseDetails(CreateOrderResponseDetails.Builder responseDetails) {
            this.responseDetails = responseDetails;
            return this;
        }

        public CreateOrderResponse build() {
            CreateOrderResponseDetails details = responseDetails.build();
            CreateOrderResponse response = new CreateOrderResponse();
            response.setResponseDetails(details);
            if(details.getOrderRejectReason() == null) {
                response.setResponseCode(ResponseCode.ACCEPTED);
            } else {
                response.setResponseCode(ResponseCode.REJECTED);
            }

            return response;
        }
    }
}
