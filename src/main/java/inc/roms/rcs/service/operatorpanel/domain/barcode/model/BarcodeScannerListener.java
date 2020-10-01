package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import inc.roms.rcs.service.operatorpanel.response.ScanProductResponse;
import inc.roms.rcs.websocket.WebsocketService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarcodeScannerListener {

    private final WebsocketService websocketService;

    @SneakyThrows
    public void notifyAboutBarcode(Barcode barcode) {
        websocketService.send(ScanProductResponse.from(barcode.getBarcode()));
    }
}
