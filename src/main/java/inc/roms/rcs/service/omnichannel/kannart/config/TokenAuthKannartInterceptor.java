package inc.roms.rcs.service.omnichannel.kannart.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthKannartInterceptor implements ClientHttpRequestInterceptor {

    private final static String MOCK_TOKEN = "a5NvNKxTxXKzmNaS0tHb4hPsHfurxErv";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBearerAuth(MOCK_TOKEN);
        return execution.execute(request, body);
    }

}
