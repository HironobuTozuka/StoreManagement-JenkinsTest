package inc.roms.rcs.websocket;

import inc.roms.rcs.service.operatorpanel.response.ScanProductResponse;
import inc.roms.rcs.service.inventory.domain.model.ToteResponse;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebsocketService {

    public static final String TOPIC_TOTE_IN_LOADING_GATE = "/topic/tote-in-loading-gate";
    public static final String TOPIC_SKU = "/topic/sku";

    private final SimpMessagingTemplate template;

    public void send(ToteResponse toteResponse) {
        template.convertAndSend(TOPIC_TOTE_IN_LOADING_GATE, toteResponse);
    }

    public void send(ScanProductResponse scanProductResponse) {
        template.convertAndSend(TOPIC_SKU, scanProductResponse);
    }
}
