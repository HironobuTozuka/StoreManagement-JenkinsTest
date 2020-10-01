package inc.roms.rcs.service.order.response;

import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import lombok.Data;

import java.util.List;

@Data
public class ListOrderResponse {

    private List<OrderDetails> orders;

    private ListResponseMetaDetails meta;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<OrderDetails> orders;

        public Builder orders(List<OrderDetails> orders) {
            this.orders = orders;
            return this;
        }

        public ListOrderResponse build() {
            ListOrderResponse response = new ListOrderResponse();
            response.setOrders(orders);
            response.setMeta(new ListResponseMetaDetails(response.orders.size()));
            return response;
        }

    }
}
