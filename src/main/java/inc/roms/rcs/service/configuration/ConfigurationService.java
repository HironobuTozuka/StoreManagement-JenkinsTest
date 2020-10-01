package inc.roms.rcs.service.configuration;

import inc.roms.rcs.service.configuration.model.Configuration;
import inc.roms.rcs.service.configuration.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository repository;
    private final ConifgurationDefaults defaults;

    public <T> T getConfigValue(ConfigKey<T> type) {
        Configuration configuration = repository
                .getByKey(type.getKey())
                .orElseGet(newConfigurationWithDefaultValue(type));

        return convertValueToSpecificType(type, configuration);
    }

    private <T> T convertValueToSpecificType(ConfigKey<T> type, Configuration configuration) {
        return type.convert(configuration.getValue());
    }

    private <T> Supplier<Configuration> newConfigurationWithDefaultValue(ConfigKey<T> configKey) {
        return () -> {
            Configuration configuration = new Configuration(configKey.getKey(), configKey.convert(defaults.getDefaultValue(configKey)));
            repository.save(configuration);
            return configuration;
        };
    }
}
