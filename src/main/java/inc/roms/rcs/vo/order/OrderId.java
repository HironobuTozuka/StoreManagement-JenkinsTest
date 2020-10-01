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
public class OrderId implements StringVO {

    @JsonValue
    private String orderId;

    @JsonCreator
    public static OrderId from(String source) {
        return new OrderId(source);
    }

    public static OrderId generate() {
        return OrderId.from(UUID.randomUUID().toString());
    }

    @Override
    public String value() {
        return orderId;
    }

}
