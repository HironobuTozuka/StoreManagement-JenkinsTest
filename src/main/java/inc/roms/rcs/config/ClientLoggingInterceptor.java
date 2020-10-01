package inc.roms.rcs.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class ClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final String clientName;

    public ClientLoggingInterceptor(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        if (log.isDebugEnabled()) {
            String message = clientName + " client, request, " + request.getMethod() + ", \"" + request.getURI() + "\"";

            String string = new String(body, "UTF-8");
            if (!string.isBlank())
                message += "\nbody:\n" + string;

            log.debug(message);
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            String message = clientName + " client, resonse " + response.getStatusCode() + ", " + response.getStatusText();
            
            String string = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            if (!string.isBlank())
                message += "\nbody:\n" + string;

            log.debug(message);
        }
    }
}
