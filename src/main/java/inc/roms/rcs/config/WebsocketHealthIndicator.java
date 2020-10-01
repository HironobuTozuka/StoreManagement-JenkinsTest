package inc.roms.rcs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebsocketHealthIndicator implements HealthIndicator {

    private final TaskExecutor clientInboundChannelExecutor;

    @Override
    public Health getHealth(boolean includeDetails) {
        Health.Builder up = Health.up();
        if (includeDetails) {
            if(clientInboundChannelExecutor instanceof ThreadPoolTaskExecutor) {
                int size = ((ThreadPoolTaskExecutor)clientInboundChannelExecutor).getThreadPoolExecutor().getQueue().size();
                int corePoolSize = ((ThreadPoolTaskExecutor)clientInboundChannelExecutor).getThreadPoolExecutor().getCorePoolSize();
                up.withDetail("Client inbound taskExecutor type", clientInboundChannelExecutor.getClass())
                  .withDetail("Core pool size", corePoolSize)
                  .withDetail("Tasks queue size", size);
            } else {
                up.withDetail("Client inbound taskExecutor type", clientInboundChannelExecutor.getClass());
            }
        }
        return up.build();
    }

    @Override
    public Health health() {
        return getHealth(true);
    }
}
