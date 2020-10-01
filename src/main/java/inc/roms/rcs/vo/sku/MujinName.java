package inc.roms.rcs.vo.sku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
public class MujinName implements StringVO {

    @JsonValue
    private String mujinName;

    @JsonCreator
    public static MujinName from(String source) {
        return new MujinName(source);
    }

    @Override
    public String toString() {
        return mujinName;
    }

    @Override
    public String value() {
        return mujinName;
    }
}

