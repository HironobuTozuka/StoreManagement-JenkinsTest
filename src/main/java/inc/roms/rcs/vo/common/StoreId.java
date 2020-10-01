package inc.roms.rcs.vo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreId {

    @JsonValue
    private String storeId;

    @JsonCreator
    public static StoreId from(String storeId) {
        return new StoreId(storeId);
    }
}
