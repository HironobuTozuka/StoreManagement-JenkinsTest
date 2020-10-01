package inc.roms.rcs.vo.sku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
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
public class ExternalId implements StringVO {

    @JsonValue
    private String externalId;

    @JsonCreator
    public static ExternalId from(String skuId) {
        return new ExternalId(skuId);
    }

    public static ExternalId generate() {
        return ExternalId.from(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return externalId;
    }

    @Override
    public String value() {
        return externalId;
    }
}
