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
public class ScheduledSupplyItemId implements StringVO {

    @JsonValue
    private String scheduledSupplyItemId;

    @JsonCreator
    public static ScheduledSupplyItemId from(String source) {
        return new ScheduledSupplyItemId(source);
    }

    public static ScheduledSupplyItemId generate() {
        return ScheduledSupplyItemId.from(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return scheduledSupplyItemId;
    }

    @Override
    public String value() {
        return scheduledSupplyItemId;
    }
}
