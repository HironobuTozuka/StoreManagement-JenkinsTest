package inc.roms.rcs.api.external.health;

import inc.roms.rcs.service.inventory.SkuBatchService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.validation.RequestNotValidException;
import inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.TransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
ttt
@RestController
@Slf4j
@RequiredArgsConstructor
public class HealthCheckController {

    @Value("${rcs.versions.hardware:POC}")
    private String hardwareVersion;

    @Value("${rcs.versions.api:1.0}")
    private String apiVersions;

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    private final HealthEndpoint healthEndpoint;

    private final BuildProperties buildProperties;

    private final ToteService toteService;

    @GetMapping("/api/health-check")
    public HealthCheckRsp response(@RequestParam(name = "transaction_id") TransactionId transactionId) {
        if (transactionId.isEmpty()) {
            throw new RequestNotValidException("Request not valid, transactionId must not be null");
        }

        boolean availableDeliveryTotes = toteService.findAllAvailableDeliveryTotes().size() > 0;
        boolean stock = toteService.findAllTotesWithAvailableStock().size() > 0;

        log.info("Health check with transaction_id: {}", transactionId);
        Details statusDetails = new Details();
        if (stock && availableDeliveryTotes) {
            statusDetails.setAcceptingOrders(true);
            statusDetails.setAcceptingPreorders(true);
            statusDetails.setDeliveringPreorders(true);
        } else {
            statusDetails.setAcceptingOrders(false);
            statusDetails.setAcceptingPreorders(false);
            statusDetails.setDeliveringPreorders(false);
        }
        HealthCheckRsp healthCheckResponse = HealthCheckRsp.builder()
                .apiVersions(apiVersions)
                .osVersion(buildProperties.getVersion())
                .hardwareVersion(hardwareVersion)
                .receiveTime(JapanTimeHelper.nowInJapan())
                .statusCode(getStatusCode())
                .storeCode(storeCode)
                .statusDetails(statusDetails).build();
        log.info("Health check with transaction_id: {}, response: {}", transactionId, healthCheckResponse);
        return healthCheckResponse;
    }

    private RcsStatus getStatusCode() {
        if (healthEndpoint.health().getStatus().equals(Status.UP))
            return RcsStatus.FUNCTIONAL;
        else if (healthEndpoint.health().getStatus().equals(Status.DOWN)) {
            return RcsStatus.ERROR;
        }
        return RcsStatus.WARN;
    }
}
