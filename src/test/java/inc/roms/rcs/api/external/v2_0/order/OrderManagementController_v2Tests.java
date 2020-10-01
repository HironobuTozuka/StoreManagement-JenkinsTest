package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.api.external.v2_0.ApiV2BaseMvcTest;
import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.order.exception.NoEmptyTotesException;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.sku.SkuId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static inc.roms.rcs.api.external.v2_0.builders.CreateOrderRequestBuilder.randomOrder;
import static inc.roms.rcs.matchers.IssueMatcher.matchesIssue;
import static inc.roms.rcs.service.order.response.CreateOrderResponse.createOrderResponse;
import static inc.roms.rcs.service.order.response.CreateOrderResponseDetails.details;
import static inc.roms.rcs.service.task.domain.model.TaskBundle.pickBundle;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OrderManagementController_v2.class})
@Import({OrderRequestResponseConverter_v2.class})
public class OrderManagementController_v2Tests extends ApiV2BaseMvcTest {

    @Test
    public void shouldReturn200IfNoExceptionIsThrown() throws Exception {
        //given
        CreateOrderRequest createOrderRequest = randomOrder().build();

        //when
        when(orderService.create(any(inc.roms.rcs.service.order.request.CreateOrderRequest.class)))
                .thenReturn(createOrderResponse().responseDetails(
                        details()
                                .gateId(createOrderRequest.getGate())
                                .eta(LocalDateTime.now())
                                .pickFuture(completedFuture(pickBundle(createOrderRequest.getOrderNo())))
                ).build());


        post(createOrderRequest).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldRespond200IfSkuIsNotFound() throws Exception {
        //given
        CreateOrderRequest createOrderRequest = randomOrder().build();
        SkuId sku = createOrderRequest.getOrderLines().get(0).getSku();
        orderServiceThrows(new SkuNotFoundException(sku));

        //when
        MvcResult mvcResult = post(createOrderRequest)
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        CreateOrderResponse createOrderResponse = getResponse(mvcResult);

        assertThat(createOrderResponse.getAcceptCode()).isEqualTo(AcceptCode.ERROR);
        assertThat(createOrderResponse.getAcceptMessage().getIssueId()).isNotNull();
        assertThat(createOrderResponse.getAcceptMessage().getErrorCode()).isEqualTo(IssueReason.UNKNOWN_SKU);
        assertThat(createOrderResponse.getAcceptMessage().getSku()).isEqualTo(sku);
    }

    @Test
    public void shouldCreateIssueIfSkuIsNotFound() throws Exception {
        //given
        CreateOrderRequest createOrderRequest = randomOrder().build();
        SkuId sku = createOrderRequest.getOrderLines().get(0).getSku();
        orderServiceThrows(new SkuNotFoundException(sku));

        //when
        MvcResult result = post(createOrderRequest)
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        CreateOrderResponse response = getResponse(result);

        //then
        verify(issueRepository).save(argThat(matchesIssue()
                .withIssueId(response.getAcceptMessage().getIssueId())
                .withReason(IssueReason.UNKNOWN_SKU)
                .withAction(IssueAction.CHECK_SKU)
                .withSkuId(sku)));

        assertThat(response.getAcceptMessage().getErrorCode()).isEqualTo(IssueReason.UNKNOWN_SKU);
    }

    private CreateOrderResponse getResponse(MvcResult result) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), CreateOrderResponse.class);
    }

    @Test
    public void shouldCreateIssueIfThereIsNoEmptyTote() throws Exception {
        //given
        CreateOrderRequest createOrderRequest = randomOrder().build();
        orderServiceThrows(new NoEmptyTotesException());

        //when
        MvcResult result = post(createOrderRequest).andExpect(status().is2xxSuccessful()).andReturn();

        CreateOrderResponse response = getResponse(result);

        //then
        verify(issueRepository).save(argThat(matchesIssue()
                        .withIssueId(response.getAcceptMessage().getIssueId())
                        .withReason(IssueReason.NO_EMPTY_TOTES)
                        .withAction(IssueAction.EMPTY_TOTES)
                )
        );
    }

    @Test
    public void shouldReturn200IfThereIsNoEmptyTote() throws Exception {
        //given
        CreateOrderRequest createOrderRequest = randomOrder().build();
        orderServiceThrows(new NoEmptyTotesException());

        //when
        MvcResult result = post(createOrderRequest).andExpect(status().is2xxSuccessful()).andReturn();

        CreateOrderResponse response = getResponse(result);

        //then
        assertThat(response.getAcceptCode()).isEqualTo(AcceptCode.ERROR);
        assertThat(response.getAcceptMessage().getIssueId()).isNotNull();
        assertThat(response.getAcceptMessage().getErrorCode()).isEqualTo(IssueReason.NO_EMPTY_TOTES);
        assertThat(response.getAcceptMessage().getOrderNo()).isEqualTo(createOrderRequest.getOrderNo());
    }

    private void orderServiceThrows(BusinessException exception) {
        when(orderService.create(any(inc.roms.rcs.service.order.request.CreateOrderRequest.class)))
                .thenThrow(exception);
    }

    private ResultActions post(CreateOrderRequest createOrderRequest) throws Exception {
        return web.perform(MockMvcRequestBuilders.post("/api/2.0/order:create")
                .content(objectMapper.writeValueAsString(createOrderRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

}
