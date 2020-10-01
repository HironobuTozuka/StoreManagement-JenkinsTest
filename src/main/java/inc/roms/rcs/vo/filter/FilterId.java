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
public class FilterId implements Serializable, StringVO {

    @JsonValue
    private String filterId;

    @JsonCreator
    public static FilterId from(String source) {
        return new FilterId(source);
    }

    @Override
    public String toString() {
        return filterId;
    }

    @Override
    public String value() {
        return filterId;
    }
}
