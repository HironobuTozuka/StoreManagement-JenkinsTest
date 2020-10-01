package inc.roms.rcs.vo.tote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ToteId {

    public static final ToteId NOREAD = ToteId.from("NOREAD");
    public static final ToteId UNKNOWN = ToteId.from("UNKNOWN");
    public static final ToteId NOTOTE = ToteId.from("NOTOTE");

    @JsonValue
    private String toteId;

    @JsonCreator
    public static ToteId from(String toteId) {
        return new ToteId(toteId);
    }

    @Override
    public String toString() {
        return toteId;
    }
}