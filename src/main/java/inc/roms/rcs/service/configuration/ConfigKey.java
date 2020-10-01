package inc.roms.rcs.service.configuration;

import inc.roms.rcs.vo.config.DbConfigKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigKey<T> implements ConfigConverter<T> {

    public final static ConfigKey<Integer> NUMBER_OF_DELIVERY_TOTES = new ConfigKey<>(DbConfigKey.from("NUMBER_OF_DTS"), new ConfigConverter.IntegerConverter());
    public static final ConfigKey<Integer> MAX_ORDER_SIZE = new ConfigKey<>(DbConfigKey.from("MAX_ORDER_SIZE"), new IntegerConverter());

    @Getter
    private final DbConfigKey key;
    private final ConfigConverter<T> converter;

    @Override
    public T convert(String s) {
        return converter.convert(s);
    }

    @Override
    public String convert(T t) {
        return converter.convert(t);
    }
}
