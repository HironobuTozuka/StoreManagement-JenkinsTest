package inc.roms.rcs.service.inventory.job;

import inc.roms.rcs.service.inventory.SkuManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkuStatusJob {

    private final SkuManagementService skuManagementService;

    @Scheduled(fixedRateString = "${inc.roms.sku_status_job.rate}")
    public void checkTechnicalData() {
        skuManagementService.checkSkuTechnicalData();
    }

}
