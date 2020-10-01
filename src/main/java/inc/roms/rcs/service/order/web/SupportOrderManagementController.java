package inc.roms.rcs.service.order.web;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.service.order.request.DeliverOrderRequest;
import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponse;
import inc.roms.rcs.service.order.response.DeliverOrderResponse;
import inc.roms.rcs.service.order.response.OrderActionResponse;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.web.model.Notifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 *  This controller shouldn't be part of ordermanagement package - it should be moved to top level package
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class SupportOrderManagementController {

    private final OrderManagementService orderManagementService;
    private final ToteService toteService;

    @GetMapping("/order/create")
    public ModelAndView create() {
        return new ModelAndView("/order/create");
    }

    @PostMapping("/order/create")
    public ModelAndView create(@ModelAttribute CreateOrderRequest request) {
        log.info("CreateOrderRequest: {}", request);
        request.setPickupTime(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        CreateOrderResponse createOrderResponse = orderManagementService.create(request);
        log.info("CreateOrderResponse: {}", createOrderResponse);
        if(createOrderResponse.getResponseCode().equals(ResponseCode.ACCEPTED)) {
            return new ModelAndView("/order/create", "notifications", Notifications.success("Order created"));
        } else {
            log.warn("Order creation failed:" + createOrderResponse);
            return new ModelAndView("/order/create", "notifications", Notifications.error(createOrderResponse.getResponseDetails().getOrderRejectReason().toString()));
        }
    }

    @GetMapping("/order/deliver")
    public ModelAndView deliver() {
        return new ModelAndView("/order/deliver");
    }

    @PostMapping("/order/deliver")
    public DeliverOrderResponse deliver(@ModelAttribute DeliverOrderRequest request) {
        log.info("DeliverOrderRequest: {}", request);
        DeliverOrderResponse deliver = orderManagementService.deliver(request);
        log.info("DliverOrderResponse: {}", deliver);
        return deliver;
    }

    @GetMapping("/order/pick")
    public ModelAndView picks() {
        return new ModelAndView("/order/picks");
    }

    @PostMapping("/order/pick")
    public OrderActionResponse picks(@ModelAttribute OrderActionRequest request) {
        log.info("pick action: OrderActionRequest: {}", request);
        OrderActionResponse orderActionResponse = orderManagementService.pickOrder(request.getOrderId());
        log.info("pick action: OrderActionResponse: {}", orderActionResponse);
        return orderActionResponse;
    }

    @GetMapping("/order/cancel")
    public ModelAndView cancel() {
        return new ModelAndView("/order/cancel");
    }

    @PostMapping("/order/cancel")
    public OrderActionResponse cancel(@ModelAttribute OrderActionRequest request) {
        log.info("cancel action: OrderActionRequest: {}", request);
        OrderActionResponse orderActionResponse = orderManagementService.cancel(request);
        log.info("cancel action: OrderActionResponse: {}", orderActionResponse);
        return orderActionResponse;
    }


    @GetMapping("/tote/empty")
    public ModelAndView emptyTotes() {
        List<Tote> allAvailableToteByTypeWithAllEmptySlots = toteService.findAllAvailableDeliveryTotes();
        return new ModelAndView("/tote/empty", "numberOfEmptyTotes", allAvailableToteByTypeWithAllEmptySlots.size());
    }


    @GetMapping("/tote/delivery")
    public ModelAndView deliveryTotes() {
        List<Tote> allAvailableToteByTypeWithAllEmptySlots = toteService.findAllWithDeliveryInventory();
        return new ModelAndView("/tote/delivery", "totes", allAvailableToteByTypeWithAllEmptySlots);
    }


    @GetMapping("/tote/clean")
    public ModelAndView clean(@RequestParam ToteId toteId) {
        toteService.clean(toteId);
        return new ModelAndView("redirect:/tote/delivery");
    }
}
