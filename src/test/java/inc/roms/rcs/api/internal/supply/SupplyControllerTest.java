package inc.roms.rcs.api.internal.supply;

import inc.roms.rcs.api.error.ReportIssueOnBusinessExceptionControllerAdvice;
import inc.roms.rcs.api.internal.ApiInternalBaseMvcTest;
import inc.roms.rcs.service.inventory.exception.MixedTemperatureRegimesException;
import inc.roms.rcs.service.operatorpanel.request.SupplyToteRequest;
import inc.roms.rcs.vo.sku.SkuId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({SupplyController.class, ReportIssueOnBusinessExceptionControllerAdvice.class})
public class SupplyControllerTest extends ApiInternalBaseMvcTest {

    @Test
    public void shouldReturn200IfToteWasRequested() throws Exception {
        //given
        SupplyToteRequest supplyToteRequest = new SupplyToteRequest();

        //when
        web.perform(MockMvcRequestBuilders.post("/api/internal/supply:request_tote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplyToteRequest)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldReturn400IfMixedTemperatureRequests() throws Exception {
        //given
        SupplyToteRequest supplyToteRequest = new SupplyToteRequest();

        //when
        when(supplyService.requestTote(supplyToteRequest)).thenThrow(new MixedTemperatureRegimesException(List.of(SkuId.from("skuid"), SkuId.from("skuid1"))));

        //when
        web.perform(MockMvcRequestBuilders.post("/api/internal/supply:request_tote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplyToteRequest)))
                .andExpect(status().isUnprocessableEntity());
    }
}
