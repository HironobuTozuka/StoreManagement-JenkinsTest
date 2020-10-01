package inc.roms.rcs.service.omnichannel.v1.config;

import inc.roms.rcs.service.omnichannel.config.OmniChannelProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@RequiredArgsConstructor
public class TokenAuth3EInterceptor implements ClientHttpRequestInterceptor {

    private String token;
    private Instant validTo;
    private final OmniChannelProperties omniChannelProperties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if(validTo == null || !validTo.isAfter(Instant.now().plus(10, SECONDS))) {
            reauthenticate();
        }
        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }

    private void reauthenticate() {
        Instant requestTime = Instant.now();
        RestTemplate restTemplate = new RestTemplate();
        AuthResponse3E authResponse = restTemplate.postForObject(
                omniChannelProperties.getTokenUri(),
                new AuthRequest3EFactory(omniChannelProperties).getRequest(),
                AuthResponse3E.class
        );
        this.token = authResponse.getAccessToken();
        this.validTo = requestTime.plus(authResponse.getExpiresIn(), SECONDS);
    }
}
