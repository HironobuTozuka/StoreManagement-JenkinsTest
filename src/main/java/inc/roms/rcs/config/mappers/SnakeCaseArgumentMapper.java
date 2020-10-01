package inc.roms.rcs.config.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SnakeCaseArgumentMapper implements HandlerMethodArgumentResolver {

    private final ObjectMapper mapper;

    @Override
    public boolean supportsParameter(final MethodParameter methodParameter) {
        return true;
    }

    @Override
    public Object resolveArgument(final MethodParameter methodParameter,
                                  final ModelAndViewContainer modelAndViewContainer,
                                  final NativeWebRequest nativeWebRequest,
                                  final WebDataBinderFactory webDataBinderFactory) throws Exception {

        final HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();
        if(request.getQueryString() != null) {
            final String json = qs2json(request.getQueryString());
            return mapper.readValue(json, methodParameter.getParameterType());
        }

        return mapper.readValue("{}", methodParameter.getParameterType());
    }

    private String qs2json(String queryString) throws JsonProcessingException {
        Map<String, String> paramMap = Splitter.on("&").withKeyValueSeparator("=").split(queryString);
        return mapper.writeValueAsString(paramMap);
    }
}
