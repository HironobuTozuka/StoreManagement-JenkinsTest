package inc.roms.rcs.api.external.v2_0.stock;

import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.api.external.v2_0.vo.AcceptMessage;
import inc.roms.rcs.service.inventory.SupplyService;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.issue.CreateIssueResponse;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.nowInJapan;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final SupplyService supplyService;
    private final ScheduleSupplyRequestResponseConverter converter;
    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final Validator validator;

    @PostMapping("/api/2.0/stock:schedule")
    public SupplyResponse schedule(@RequestBody ScheduleSupplyRequest supplyRequest) {
        validator.validate(supplyRequest);
        try {
            return converter.convert(
                    supplyService.scheduleSupply(
                            converter.toBaseRequest(supplyRequest)
                    )
            );
        } catch (SkuNotFoundException snfe) {
            CreateIssueRequest createIssueRequest = snfe.toIssue(issueFactory);
            CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);
            SupplyResponse supplyResponse = new SupplyResponse();
            supplyResponse.setAcceptCode(AcceptCode.ERROR);
            supplyResponse.setReceiveTime(nowInJapan());
            supplyResponse.setAcceptMessage(AcceptMessage.builder()
                    .issueId(createIssueResponse.getDetails().getIssueId())
                    .sku(snfe.getSkuId())
                    .errorCode(createIssueRequest.getReason())
                    .build());
            return supplyResponse;
        }
    }

    @PostMapping("/api/2.0/stock:dispose")
    public SupplyResponse dispose(@RequestBody DisposeStockRequest disposeStockRequest) {
        validator.validate(disposeStockRequest);
        return converter.convert(supplyService.batchDispose(
                converter.toBaseRequest(disposeStockRequest)
        ));
    }

}
