package inc.roms.rcs.vo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.task.TaskId;
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
public class RcsOperationId {

    @JsonValue
    private String source;

    @JsonCreator
    public static RcsOperationId from(String source) {
        return new RcsOperationId(source);
    }

    public static RcsOperationId generate() {
        return RcsOperationId.from(UUID.randomUUID().toString());
    }

}
