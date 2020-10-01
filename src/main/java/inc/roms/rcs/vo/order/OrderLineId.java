package inc.roms.rcs.vo.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineId implements StringVO {

    @JsonValue
    private String orderLineId;

    @JsonCreator
    public static OrderLineId from(String source) {
        return new OrderLineId(source);
    }

    public static OrderLineId generate() {
        return OrderLineId.from(UUID.randomUUID().toString());
    }

    @Override
    public String value() {
        return orderLineId;
    }
}
