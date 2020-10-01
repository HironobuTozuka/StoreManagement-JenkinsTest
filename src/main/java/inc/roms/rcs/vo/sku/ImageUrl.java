package inc.roms.rcs.vo.sku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
public class ImageUrl implements StringVO {

    @JsonValue
    private String imageUrl;

    @JsonCreator
    public static ImageUrl from(String skuId) {
        return new ImageUrl(skuId);
    }

    @Override
    public String toString() {
        return imageUrl;
    }

    @Override
    public String value() {
        return imageUrl;
    }
}
