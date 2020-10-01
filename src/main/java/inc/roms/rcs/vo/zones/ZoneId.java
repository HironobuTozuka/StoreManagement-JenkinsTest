package inc.roms.rcs.vo.zones;

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
public class ZoneId implements StringVO {

    @JsonValue
    private String zoneId;

    @JsonCreator
    public static ZoneId from(String source) {
        return new ZoneId(source);
    }

    @Override
    public String toString() {
        return zoneId;
    }

    @Override
    public String value() {
        return zoneId;
    }
}
