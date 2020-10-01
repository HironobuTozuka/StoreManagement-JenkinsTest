package inc.roms.rcs.vo.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.persistence.Embeddable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
@ToString
public class TaskId {

    @JsonValue
    private String taskId;

    @JsonCreator
    public static TaskId from(String source) {
        return new TaskId(source);
    }

    public static TaskId generate() {
        return TaskId.from(UUID.randomUUID().toString());
    }
}
