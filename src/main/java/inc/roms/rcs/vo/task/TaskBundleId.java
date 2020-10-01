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
public class TaskBundleId {

    @JsonValue
    private String taskBundleId;

    @JsonCreator
    public static TaskBundleId from(String source) {
        return new TaskBundleId(source);
    }

    public static TaskBundleId generate() {
        return TaskBundleId.from(UUID.randomUUID().toString());
    }
}
