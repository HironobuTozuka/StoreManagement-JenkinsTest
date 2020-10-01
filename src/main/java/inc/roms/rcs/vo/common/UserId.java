package inc.roms.rcs.vo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserId {

    @JsonValue
    private String userId;

    @JsonCreator
    public static UserId from(String userId) {
        return new UserId(userId);
    }

    public static UserId generate() {
        return UserId.from(UUID.randomUUID().toString());
    }
}
