package inc.roms.rcs.vo.security;

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
public class AuthToken {

    @JsonValue
    private String authToken;

    @JsonCreator
    public static AuthToken from(String source) {
        return new AuthToken(source);
    }

    public static AuthToken generate() {
        return AuthToken.from(UUID.randomUUID().toString());
    }
}
