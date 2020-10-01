package inc.roms.rcs.service.filter.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import inc.roms.rcs.vo.filter.FilterFields;

public class FilterFieldsJsonToStringDeserializer extends StdDeserializer<FilterFields> {

    public FilterFieldsJsonToStringDeserializer() {
        super(String.class);
    }

    @Override
    public FilterFields deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return FilterFields.from(parser.getCodec().readTree(parser).toString());
    }
}