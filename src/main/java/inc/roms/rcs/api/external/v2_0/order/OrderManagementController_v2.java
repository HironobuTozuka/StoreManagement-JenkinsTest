package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.api.external.v2_0.vo.AcceptMessage;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.exception.OrderTooBigException;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.issue.CreateIssueResponse;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.service.order.exception.NotEnoughSkuToFulfillOrderException;
import inc.roms.rcs.service.order.exception.OrderAlreadyExistsException;
import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.order.response.OrderActionResponse;
import inc.roms.rcs.validation.Validator;
import inc.roms.rcs.vo.common.StoreId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.nowInJapan;
import static inc.roms.rcs.vo.issue.IssueReason.ORDER_ALREADY_EXISTS;

@Slf4j
@RestController
@RequestMapping("/api/2.0")
@RequiredArgsConstructor
public class OrderManagementController_v2 {

    private final OrderManagementService orderService;
    private final OrderRequestResponseConverter_v2 converter;
    private final IssueFactory issueFactory;
    private final IssueService issueService;
    private final Validator validator;

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    @PostMapping(value = "order:create")
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
        validator.validate(request);
        CreateOrderResponse createOrderResponse;
        try {
            log.info("CreateOrderRequest: {}", request);
            createOrderResponse = converter.convert(orderService.create(converter.toBaseRequest(request)));
            log.info("CreateOrderResponse: {}", createOrderResponse);
            return createOrderResponse;
        } catch (NotEnoughSkuToFulfillOrderException exception) {
            CreateIssueRequest createIssueRequest = exception.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            createOrderResponse = CreateOrderResponse.builder()
                    .acceptCode(AcceptCode.ERROR)
                    .receiveTime(nowInJapan())
                    .storeCode(storeCode)
                    .acceptMessage(AcceptMessage.builder()
                            .issueId(createIssueResponse.getDetails().getIssueId())
                            .errorCode(createIssueRequest.getReason())
                            .sku(createIssueRequest.getSkuId())
                            .build())
                    .build();
        } catch (NoEmptyTotesException exception) {
            CreateIssueRequest createIssueRequest = exception.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            createOrderResponse = CreateOrderResponse.builder()
                    .acceptCode(AcceptCode.ERROR)
                    .receiveTime(nowInJapan())
                    .storeCode(storeCode)
                    .acceptMessage(AcceptMessage.builder()
                            .issueId(createIssueResponse.getDetails().getIssueId())
                            .errorCode(createIssueRequest.getReason())
                            .orderNo(request.getOrderNo())
                            .build())
                    .build();
        } catch (SkuNotFoundException exception) {
            CreateIssueRequest createIssueRequest = exception.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            createOrderResponse = CreateOrderResponse.builder()
                    .acceptCode(AcceptCode.ERROR)
                    .receiveTime(nowInJapan())
                    .storeCode(storeCode)
                    .acceptMessage(AcceptMessage.builder()
                            .issueId(createIssueResponse.getDetails().getIssueId())
                            .errorCode(createIssueRequest.getReason())
                            .sku(exception.getSkuId())
                            .build())
                    .build();
        } catch (OrderTooBigException toBig) {
            CreateIssueRequest createIssueRequest = toBig.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            createOrderResponse = CreateOrderResponse.builder()
                    .acceptCode(AcceptCode.ERROR)
                    .receiveTime(nowInJapan())
                    .storeCode(storeCode)
                    .acceptMessage(AcceptMessage.builder()
                            .issueId(createIssueResponse.getDetails().getIssueId())
                            .errorCode(createIssueRequest.getReason())
                            .build())
                    .build();
        } catch (OrderAlreadyExistsException exception) {
            createOrderResponse = CreateOrderResponse.builder()
                    .acceptCode(AcceptCode.ERROR)
                    .receiveTime(nowInJapan())
                    .storeCode(storeCode)
                    .acceptMessage(AcceptMessage.builder()
                            .errorCode(ORDER_ALREADY_EXISTS)
                            .orderNo(request.getOrderNo())
                            .build())
                    .build();
        }
        return createOrderResponse;
    }

    @PostMapping("order:deliver")
    public DeliverOrderResponse deliver(@RequestBody DeliverOrderRequest request) {
        validator.validate(request);
        log.info("DeliverOrderRequest: {}", request);
        DeliverOrderResponse deliver;
        try {
            deliver = converter.convert(orderService.deliver(converter.convert(request)), request);
        } catch (IssueCreatingBusinessException exception) {
            CreateIssueRequest createIssueRequest = exception.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            deliver = new DeliverOrderResponse();
            deliver.setAcceptCode(AcceptCode.ERROR);
            deliver.setAcceptMessage(AcceptMessage.builder()
                    .issueId(createIssueResponse.getDetails().getIssueId())
                    .orderNo(createIssueRequest.getOrderId())
                    .errorCode(createIssueRequest.getReason())
                    .build());
        }
        log.info("DeliverOrderResponse: {}", deliver);
        return deliver;
    }

    @PostMapping("order:cancel")
    public OrdersActionResponse cancel(@RequestBody OrdersActionRequest_v2 request) {
        validator.validate(request);
        log.info("dispose action: OrdersActionRequest: {}", request);
        OrdersActionResponse response = converter.convert(orderService.batchCancel(converter.toBatchActionRequest(request)));
        log.info("dispose action: OrdersActionResponse: {}", response);
        return response;
    }

    @PostMapping("order:dispose")
    public OrdersActionResponse dispose(@RequestBody OrdersActionRequest_v2 request) {
        validator.validate(request);
        log.info("dispose action: OrdersActionRequest: {}", request);
        OrdersActionResponse response = converter.convert(orderService.batchDispose(converter.toBatchActionRequest(request)));
        log.info("dispose action: OrdersActionResponse: {}", response);
        return response;
    }
}
