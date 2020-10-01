package inc.roms.rcs.api.external.v2_0.action;

import inc.roms.rcs.api.error.annotations.ReportIssueOnBusinessException;
import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.validation.Validator;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.vo.common.StoreId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.*;

@Slf4j
@RestController
@ReportIssueOnBusinessException
@RequiredArgsConstructor
public class ActionController {

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    private final MachineOperatorService machineOperatorService;

    private final Validator validator;

    @PostMapping("/api/2.0/action:execute")
    public ExecuteActionResponse execute(@RequestBody ExecuteActionRequest request) {
        validator.validate(request);
        machineOperatorService.closeGate(request.getActionDetails().getGate(), request.getTransactionId());
        return ExecuteActionResponse.builder()
                .acceptCode(AcceptCode.SUCCESS)
                .receiveTime(nowInJapan())
                .storeCode(storeCode)
                .build();
    }
}
