package inc.roms.rcs.api.internal.order;

import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.request.BatchOrderActionRequest;
import inc.roms.rcs.service.order.response.BatchOrderActionResponse;
import inc.roms.rcs.service.order.response.DeliverOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderCrudController {

    private final OrderService orderService;

    @PostMapping("/api/internal/order:delete")
    public BatchOrderActionResponse delete(@RequestBody BatchOrderActionRequest deliverOrderRequest) {
        return orderService.delete(deliverOrderRequest);
    }

}
