package inc.roms.rcs.api.internal.tote;

import inc.roms.rcs.api.error.ReportIssueOnBusinessExceptionControllerAdvice;
import inc.roms.rcs.api.error.model.ApiError;
import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.api.internal.ApiInternalBaseMvcTest;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.operatorpanel.exception.NoSpaceForStockException;
import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import inc.roms.rcs.service.operatorpanel.request.StorageSlotModel;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static inc.roms.rcs.matchers.IssueMatcher.matchesIssue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ToteInternalController.class, ReportIssueOnBusinessExceptionControllerAdvice.class})
public class ToteInternalControllerTest extends ApiInternalBaseMvcTest {

    @Test
    public void shouldReportIssueWhenTryingToInductUnknownTote() throws Exception {
        InductRequest request = getInductRequest();

        when(loadingGateService.induct(any(InductRequest.class))).thenThrow(new ToteNotFoundException(request.getToteId()));

        post(request);

        verify(issueReporterService).report(argThat(matchesIssue()
                .withAction(IssueAction.CHECK_TOTE)
                .withReason(IssueReason.UNKNOWN_TOTE)
                .withToteId(request.getToteId())
        ));
    }

    @Test
    public void shouldReturnApiErrorWhenTryingToInductUnknownTote() throws Exception {
        InductRequest request = getInductRequest();
        ToteNotFoundException toteNotFoundException = new ToteNotFoundException(request.getToteId());

        when(loadingGateService.induct(any(InductRequest.class))).thenThrow(toteNotFoundException);

        MvcResult result = post(request);

        ApiError apiError = getApiError(result);

        assertThat(apiError.getErrorCode()).isEqualTo(RcsErrorCode.RESOURCE_NOT_FOUND);
        assertThat(apiError.getErrorMessage()).isEqualTo(toteNotFoundException.getMessage());
    }

    private ApiError getApiError(MvcResult result) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
    }


    @Test
    public void shouldReportIssueWhenThereIsNoSpaceForStock() throws Exception {
        //given
        InductRequest request = getInductRequest();

        when(loadingGateService.induct(any(InductRequest.class))).thenThrow(new NoSpaceForStockException(request.getSlots().get(0).getSkuId()));

        //when
        post(request);

        verify(issueReporterService).report(argThat(
                matchesIssue()
                        .withReason(IssueReason.NO_SPACE_FOR_STOCK)
                        .withAction(IssueAction.CHECK_SKU)
                        .withSkuId(request.getSlots().get(0).getSkuId()))
        );
    }

    @Test
    public void shouldReturnApiErrorWhenThereIsNoSpaceForStock() throws Exception {
        //given
        InductRequest request = getInductRequest();
        NoSpaceForStockException noSpaceForStockException = new NoSpaceForStockException(request.getSlots().get(0).getSkuId());

        //when
        when(loadingGateService.induct(any(InductRequest.class))).thenThrow(noSpaceForStockException);

        MvcResult result = post(request);

        ApiError apiError = getApiError(result);

        assertThat(apiError.getErrorCode()).isEqualTo(RcsErrorCode.RESOURCE_NOT_AVAILABLE);
        assertThat(apiError.getErrorMessage()).isEqualTo(noSpaceForStockException.getMessage());
    }

    private MvcResult post(InductRequest request) throws Exception {
        return web.perform(MockMvcRequestBuilders.post("/api/internal/tote:induct")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    private InductRequest getInductRequest() {
        InductRequest request = new InductRequest();
        ToteId toteId = ToteId.from("10000010");
        request.setToteId(toteId);
        List<StorageSlotModel> slots = new ArrayList<>();
        StorageSlotModel slot = new StorageSlotModel();
        slot.setSkuId(SkuId.from("1000"));
        slot.setQuantity(Quantity.of(10));
        slot.setOrdinal(0);
        slots.add(slot);
        request.setSlots(slots);
        return request;
    }
}
