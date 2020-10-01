package inc.roms.rcs.vo.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbConfigKey implements StringVO, Serializable {

    @JsonValue
    private String key;

    @JsonCreator
    public static DbConfigKey from(String source) {
        return new DbConfigKey(source);
    }

    @Override
    public String value() {
        return key;
    }

}
