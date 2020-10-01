package inc.roms.rcs.service.machineoperator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "inc.roms.machineoperator")
@Data
@Component
public class MachineOperatorProperties {

    private String url;
    private boolean enabled;

}
