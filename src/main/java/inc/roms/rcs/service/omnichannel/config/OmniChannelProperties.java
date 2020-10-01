package inc.roms.rcs.service.omnichannel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "inc.roms.omnichannel")
@Data
@Component
public class OmniChannelProperties {

    private String tokenUri;
    private String username;
    private String password;
    private String url;
    private String clientId;
    private String clientPassword;
    private boolean enabled;

}
