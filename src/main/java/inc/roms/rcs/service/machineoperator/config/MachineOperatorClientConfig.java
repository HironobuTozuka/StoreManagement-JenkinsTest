package inc.roms.rcs.service.machineoperator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.roms.rcs.config.ClientLoggingInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MachineOperatorClientConfig {

    @Bean
    public RestTemplate machineOperatorRestTemplate(ObjectMapper objectMapper, MachineOperatorProperties properties) {
        return new RestTemplateBuilder()
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .rootUri(properties.getUrl())
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .additionalInterceptors(new ClientLoggingInterceptor("MHE"))
                .build();
    }


}