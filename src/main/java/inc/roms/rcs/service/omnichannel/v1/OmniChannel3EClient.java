package inc.roms.rcs.service.omnichannel.v1;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.omnichannel.v1.model.OrderStatusChangedRequest;
import inc.roms.rcs.service.omnichannel.v1.model.UpdateInventoryRequest;
import inc.roms.rcs.web.model.Inventarization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OmniChannel3EClient {

    private final RestTemplate omniChannelRestTemplate;
    private final FeatureFlagService featureFlagService;

    public void send(Inventarization inventarization) {
        sendInternal(inventarization, "/warehouse-api/api/warehouse/2de9a0c3-4b21-407c-83d1-031ea0735eb3/inventory");
    }

    public void send(UpdateInventoryRequest updateInventoryRequest) {
        sendInternal(updateInventoryRequest, "/roms-app/api/stock/update");
    }

    public void send(OrderStatusChangedRequest orderStatusChangedRequest) {
        sendInternal(orderStatusChangedRequest, "/roms-app/api/order/" + orderStatusChangedRequest.getOrderId().getOrderId() + "/state");
    }


    private void sendInternal(Object requestBody, String url) {
        try {
            if (featureFlagService.isOmniChannelEnabled()) {
                omniChannelRestTemplate.postForEntity(url,
                        requestBody,
                        Object.class);
            } else {
                log.info("Omni channel communication is disabled, not sending: {}", requestBody);
            }
        } catch (Exception ex) {
            log.error("Couldn't execute omni channel request", ex);
        }
    }

}
