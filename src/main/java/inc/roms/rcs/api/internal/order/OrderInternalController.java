package inc.roms.rcs.api.internal.order;

import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.request.DeliverOrderRequest;
import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.order.response.DeliverOrderResponse;
import inc.roms.rcs.service.order.response.OrderActionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderManagementService orderManagementService;

    @PostMapping("/api/internal/order:deliver")
    public DeliverOrderResponse deliverOrder(@RequestBody DeliverOrderRequest deliverOrderRequest) {
        return orderManagementService.deliver(deliverOrderRequest);
    }

    @PostMapping("/api/internal/order:retrieve")
    public DeliverOrderResponse retrieveOrder(@RequestBody DeliverOrderRequest deliverOrderRequest) {
        return orderManagementService.retrieve(deliverOrderRequest);
    }

    @PostMapping("/api/internal/order:retrieve-by-tote")
    public DeliverOrderResponse retrieveOrderByToteId(@RequestBody ToteRequest toteRequest) {
        return orderManagementService.retrieveByTote(toteRequest);
    }

    @PostMapping("/api/internal/order:cancel")
    public OrderActionResponse cancel(@RequestBody OrderActionRequest request) {
        return orderManagementService.cancel(request);
    }

}
