package inc.roms.rcs.api.internal.tote;


import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.BatchToteActionRequest;
import inc.roms.rcs.service.inventory.domain.model.BatchToteActionResponse;
import inc.roms.rcs.service.inventory.request.ToteListRequest;
import inc.roms.rcs.service.inventory.response.ToteDetails;
import inc.roms.rcs.service.inventory.response.ToteListResponse;
import inc.roms.rcs.service.inventory.response.ToteTechnicalData;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
//FIXME add response / requests tests
//FIXME add validation
public class ToteCrudController {

    private final ToteService toteService;

    @GetMapping("/api/internal/tote:technical_details")
    public ToteTechnicalData technicalDetails(@RequestParam(name = "tote_id") ToteId toteId) {
        log.info("Requested details of tote with id: {}", toteId);
        ToteTechnicalData toteDetails = toteService.getToteDetails(toteId);
        log.info("Details for tote with id {} : {}", toteId, toteDetails);
        return toteDetails;
    }

    @GetMapping("/api/internal/tote/{toteId}/technical_details")
    public ToteTechnicalData technicalDetailsPath(@PathVariable ToteId toteId) {
        log.info("Requested details of tote with id: {}", toteId);
        ToteTechnicalData toteDetails = toteService.getToteDetails(toteId);
        log.info("Details for tote with id {} : {}", toteId, toteDetails);
        return toteDetails;
    }

    @GetMapping("/api/internal/tote:list")
    public ToteListResponse list(ToteListRequest toteListRequest) {
        return toteService.list(toteListRequest);
    }

    @GetMapping("/api/internal/tote:details")
    public ToteDetails details(ToteId toteId) {
        return toteService.get(toteId);
    }

    @PostMapping("/api/internal/tote:delete")
    public BatchToteActionResponse delete(@RequestBody BatchToteActionRequest toteBatchRequest) {
        return toteService.delete(toteBatchRequest);
    }

}
