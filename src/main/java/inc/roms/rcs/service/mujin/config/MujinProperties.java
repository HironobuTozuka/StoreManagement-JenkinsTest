package inc.roms.rcs.service.mujin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "inc.roms.mujin")
@Data
@Component
public class MujinProperties {

    private String url;
    private String username;
    private String password;
    private boolean enabled;

}
