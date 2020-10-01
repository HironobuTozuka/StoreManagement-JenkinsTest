package inc.roms.rcs.vo.issue;


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
public class Notes implements StringVO {

    @JsonValue
    private String notes;

    @JsonCreator
    public static Notes from(String source) {
        return new Notes(source);
    }

    @Override
    public String value() {
        return notes;
    }
}
