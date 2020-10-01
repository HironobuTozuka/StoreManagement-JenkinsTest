package inc.roms.rcs.service.omnichannel.v1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.roms.rcs.config.ClientLoggingInterceptor;
import inc.roms.rcs.service.omnichannel.config.OmniChannelProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static inc.roms.rcs.service.omnichannel.OmniChannelProfiles.NOT_COMMERCE_PLATFORM;

@Configuration
@Profile(NOT_COMMERCE_PLATFORM)
public class OmniChannel3EClientConfig {

    @Bean
    public RestTemplate omniChannelRestTemplate(ObjectMapper objectMapper,
                                                TokenAuth3EInterceptor tokenAuthInterceptor,
                                                OmniChannelProperties properties) {
        return new RestTemplateBuilder()
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .rootUri(properties.getUrl())
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .additionalInterceptors(tokenAuthInterceptor, new ClientLoggingInterceptor("3E"))
                .build();
    }


}