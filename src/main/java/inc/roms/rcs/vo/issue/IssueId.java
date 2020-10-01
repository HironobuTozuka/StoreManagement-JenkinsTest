package inc.roms.rcs.vo.issue;

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
public class IssueId implements StringVO {

    @JsonValue
    private String issueId;

    @JsonCreator
    public static IssueId from(String source) {
        return new IssueId(source);
    }

    @Override
    public String value() {
        return issueId;
    }

    public static IssueId generate() {
        return from(UUID.randomUUID().toString());
    }
}
