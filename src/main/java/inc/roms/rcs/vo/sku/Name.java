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
public class Name implements StringVO {

    @JsonValue
    private String name;

    @JsonCreator
    public static Name from(String source) {
        return new Name(source);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String value() {
        return name;
    }
}

