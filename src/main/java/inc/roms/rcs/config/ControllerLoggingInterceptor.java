package inc.roms.rcs.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ControllerLoggingInterceptor extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        log.info("Controller received: " + request.getMethod() + " " + request.getRequestURI() + " : \n"
                +  new String(requestWrapper.getContentAsByteArray(), "UTF-8") +
                "\n Sent response: " + response.getStatus() + " :\n" +
                new String(responseWrapper.getContentAsByteArray(), "UTF-8"));

        responseWrapper.copyBodyToResponse();
    }
}
