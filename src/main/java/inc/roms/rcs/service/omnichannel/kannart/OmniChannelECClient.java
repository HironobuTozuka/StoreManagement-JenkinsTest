package inc.roms.rcs.service.omnichannel.kannart;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.omnichannel.kannart.model.ErrorReport;
import inc.roms.rcs.service.omnichannel.kannart.model.OrderStatusChangedRequest;
import inc.roms.rcs.service.omnichannel.kannart.model.ProductReadyRequest;
import inc.roms.rcs.service.omnichannel.kannart.model.UpdateInventoryRequest;
import inc.roms.rcs.web.model.Inventarization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OmniChannelECClient {

    private final RestTemplate omniChannelRestTemplate;
    private final FeatureFlagService featureFlagService;

    public void send(Inventarization inventarization) {
        log.info("Omni channel inventarization is not implemented, not sending: {}", inventarization);
    }

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void send(ErrorReport errorReport) {
        sendRequest("/api/1.0/cb:action", errorReport, HttpMethod.POST);
    }

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void send(UpdateInventoryRequest updateInventoryRequest) {
        sendRequest("/api/1.0/stock:update", updateInventoryRequest, HttpMethod.PATCH);
    }

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void send(OrderStatusChangedRequest orderStatusChangedRequest) {
        sendRequest("/api/1.0/cb:action", orderStatusChangedRequest, HttpMethod.POST);
    }

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void send(ProductReadyRequest request) {
        sendRequest("/api/1.0/product:ready", request, HttpMethod.PATCH);
    }

    private void sendRequest(String url, @Nullable Object request, HttpMethod patch) throws RestClientException {
        log.info("Sending to OmniChannel url {}, body {}", url, request);
        if (featureFlagService.isOmniChannelEnabled()) {
            RequestCallback requestCallback = omniChannelRestTemplate.httpEntityCallback(request);
            omniChannelRestTemplate.execute(url, patch, requestCallback, null);
        } else {
            log.info("Omni channel communication is disabled, not sending: {}", request);
        }
    }

}
