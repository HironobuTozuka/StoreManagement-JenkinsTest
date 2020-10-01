package inc.roms.rcs.service.operatorpanel.domain.barcode.service;

import inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeScanner;
import inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeScannerListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BarcodeScannerService {

    private final List<BarcodeScanner> barcodeScanners;
    private final BarcodeScannerListener barcodeScannerListener;

    @PostConstruct
    public void init() {
        barcodeScanners.forEach(it -> it.registerListener(barcodeScannerListener));
    }

    @PreDestroy
    public void destroy() {
        barcodeScanners.forEach(BarcodeScanner::close);
    }
}
