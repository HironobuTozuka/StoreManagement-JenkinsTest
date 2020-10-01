package inc.roms.rcs.service.omnichannel.kannart.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.roms.rcs.config.ClientLoggingInterceptor;
import inc.roms.rcs.service.omnichannel.config.OmniChannelProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static inc.roms.rcs.service.omnichannel.OmniChannelProfiles.COMMERCE_PLATFORM;

@Configuration
@Profile(COMMERCE_PLATFORM)
public class OmniChannelKannartClientConfig {

    private static final int TIMEOUT = 10 * 1000;

    @Bean
    public RestTemplate omniChannelRestTemplate(ObjectMapper objectMapper,
                                                TokenAuthKannartInterceptor tokenAuthInterceptor,
                                                OmniChannelProperties properties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT);
        requestFactory.setReadTimeout(TIMEOUT);
        return new RestTemplateBuilder()
                .requestFactory(() -> new BufferingClientHttpRequestFactory(requestFactory))
                .rootUri(properties.getUrl())
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .additionalInterceptors(tokenAuthInterceptor, new ClientLoggingInterceptor("CP"))
                .build();
    }


}