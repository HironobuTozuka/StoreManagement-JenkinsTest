package inc.roms.rcs.service.operatorpanel.domain.barcode.service;

import inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class BarcodeScannerRunnerService {

    private ExecutorService service = Executors.newFixedThreadPool(4);
    private final List<BarcodeScanner> scanners;


    @PreDestroy
    public void destroy(){
        service.shutdownNow();
    }

    @PostConstruct
    public void init() {
        scanners.forEach(service::submit);
    }

}
