package inc.roms.rcs.service.machineoperator;

import inc.roms.rcs.service.featureflags.FeatureFlagService;
import inc.roms.rcs.service.machineoperator.exception.MachineOperatorException;
import inc.roms.rcs.service.machineoperator.exception.MachineOperatorUnavailableException;
import inc.roms.rcs.service.machineoperator.model.*;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static inc.roms.rcs.vo.zones.ZoneFunction.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MachineOperatorClient {

    public static final String ZONES_STATUS_URL = "/api/internal/zone:list";
    private final RestTemplate machineOperatorRestTemplate;
    private final FeatureFlagService featureFlagService;

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void executeBundle(ExecuteTaskBundleRequest executeTaskBundleRequest) {
        postInternal(executeTaskBundleRequest, "/api/internal/task-bundle:execute");
    }

    @Retryable(maxAttempts = 30, backoff = @Backoff(multiplier = 2, maxDelay = 2000, delay = 100))
    public void updateBundle(ExecuteTaskBundleRequest executeTaskBundleRequest) {
        postInternal(executeTaskBundleRequest, "/api/internal/task-bundle:update");
    }

    public void turnLightsOn(LedsRequest ledsRequest) {
        postInternal(ledsRequest, "/api/internal/action:led");
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(delay = 300))
    public void openGate(GateRequest gateRequest) {
        postInternal(gateRequest, "/api/internal/action:open");
    }

    @Retryable(maxAttempts = 20, backoff = @Backoff(delay = 100))
    public void closeGate(GateRequest gateRequest) {
        postInternal(gateRequest, "/api/internal/action:close");
    }

    private void postInternal(Object request, String url) {
        try {
            if (featureFlagService.isMachineOperatorEnabled()) {
                log.info("Sending to MHEOperator url {}, body {}", url, request);
                machineOperatorRestTemplate.postForEntity(url, request, Object.class);
            } else {
                log.debug("Machine operator is disabled! Not sending: {}", request);
            }
        } catch (ResourceAccessException connectException) {
            throw new MachineOperatorUnavailableException(connectException);
        } catch (HttpServerErrorException.InternalServerError ise) {
            throw new MachineOperatorException(ise);
        }
    }

    public ZonesStatus getZones() {
        if (featureFlagService.isMachineOperatorEnabled()) {
            log.debug("Requesting zones from url {}", ZONES_STATUS_URL);
            return machineOperatorRestTemplate.getForObject(ZONES_STATUS_URL, ZonesStatus.class);
        }
        List<ZoneState> states = new ArrayList<>();
        states.add(new ZoneState(ZoneId.from("STAGING"), TemperatureRegime.ANY, List.of(STAGING), Quantity.of(20)));
        states.add(new ZoneState(ZoneId.from("AMBIENT"), TemperatureRegime.AMBIENT, List.of(STORAGE), Quantity.of(20)));
        states.add(new ZoneState(ZoneId.from("CHILL"), TemperatureRegime.CHILL, List.of(STORAGE), Quantity.of(20)));
        states.add(new ZoneState(ZoneId.from("PLACE"), TemperatureRegime.ANY, List.of(PLACE), Quantity.of(20)));
        states.add(new ZoneState(ZoneId.from("LOADING_GATE"), TemperatureRegime.ANY, List.of(LOADING_GATE), Quantity.of(1)));
        return new ZonesStatus(states);
    }

    public void cancel(CancelTaskBundleRequest request) {
        postInternal(request, "/api/internal/task-bundle:cancel");
    }
}