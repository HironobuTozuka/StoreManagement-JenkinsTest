package inc.roms.rcs.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConifgurationDefaults {

    @Value("${inc.roms.rcs.defaults.number-of-delivery-totes:24}")
    private String numberOfDts;

    @Value("${inc.roms.rcs.defaults.max-order-size:24}")
    private String maxOrderSize;

    private Map<ConfigKey<?>, String> defaults = new HashMap<>();

    @PostConstruct
    private void init() {
        defaults.put(ConfigKey.NUMBER_OF_DELIVERY_TOTES, numberOfDts);
        defaults.put(ConfigKey.MAX_ORDER_SIZE, maxOrderSize);
    }

    public <T> T getDefaultValue(ConfigKey<T> key) {
        return key.convert(defaults.get(key));
    }
}
