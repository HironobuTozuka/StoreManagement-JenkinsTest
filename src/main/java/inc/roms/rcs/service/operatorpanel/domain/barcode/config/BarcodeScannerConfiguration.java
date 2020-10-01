package inc.roms.rcs.service.operatorpanel.domain.barcode.config;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeScanner;
import inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeScannerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = BarcodeScannersUsbProperties.class)
@AllArgsConstructor
public class BarcodeScannerConfiguration {

    private final BarcodeScannersUsbProperties properties;
    private final FeatureFlagService featureFlagService;

    @Bean
    @Autowired
    public List<BarcodeScanner> barcodeScanners(BarcodeScannerFactory barcodeScannerFactory) {
        if(featureFlagService.usbDevicesEnabled()) {
            List<BarcodeScanner> scanners = barcodeScannerFactory.createScanners(properties);
            //init
            scanners.forEach(BarcodeScanner::connectWithPhysicalDevice);
            return scanners;
        } else {
            log.info("USB barcode scanners are disabled!");
            return new ArrayList<>();
        }
    }

}
