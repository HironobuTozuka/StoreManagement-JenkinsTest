package inc.roms.rcs.vo.supply;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
public class SupplyId implements StringVO {

    @JsonValue
    private String supplyId;

    @JsonCreator
    public static SupplyId from(String source) {
        return new SupplyId(source);
    }

    public static SupplyId generate() {
        return SupplyId.from(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return supplyId;
    }

    @Override
    public String value() {
        return supplyId;
    }
}
