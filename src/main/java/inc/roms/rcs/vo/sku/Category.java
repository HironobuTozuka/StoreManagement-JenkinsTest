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
public class Category implements StringVO {

    @JsonValue
    private String Category;

    @JsonCreator
    public static Category from(String source) {
        return new Category(source);
    }

    @Override
    public String toString() {
        return Category;
    }

    @Override
    public String value() {
        return Category;
    }

}
