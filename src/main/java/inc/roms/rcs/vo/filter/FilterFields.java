package inc.roms.rcs.vo.filter;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
public class FilterFields implements Serializable, StringVO {

    @JsonValue
    private String fields;

    @JsonCreator
    public static FilterFields from(String source) {
        return new FilterFields(source);
    }

    @Override
    public String toString() {
        return fields;
    }

    @Override
    public String value() {
        return fields;
    }
}
