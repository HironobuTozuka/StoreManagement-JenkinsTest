package inc.roms.rcs.vo.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateId implements StringVO {

    public static GateId LOADING_GATE = GateId.from("LOADING_GATE");

    @JsonValue
    private String gateId;

    @JsonCreator
    public static GateId from(String source) {
        return new GateId(source);
    }

    @Override
    public String value() {
        return gateId;
    }
}
