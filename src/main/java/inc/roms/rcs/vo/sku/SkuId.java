package inc.roms.rcs.vo.sku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
public class SkuId implements Serializable, StringVO {

    @JsonValue
    private String skuId;

    @JsonCreator
    public static SkuId from(String source) {
        return new SkuId(source);
    }

    @Override
    public String toString() {
        return skuId;
    }

    @Override
    public String value() {
        return skuId;
    }
}
