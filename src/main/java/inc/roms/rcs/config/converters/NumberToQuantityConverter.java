package inc.roms.rcs.config.converters;

import inc.roms.rcs.vo.common.Quantity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class NumberToQuantityConverter implements Converter<String, Quantity> {

    @Override
    public Quantity convert(String source) {
        if(StringUtils.isEmpty(source)) return null;
        return Quantity.of(Integer.parseInt(source));
    }

}
