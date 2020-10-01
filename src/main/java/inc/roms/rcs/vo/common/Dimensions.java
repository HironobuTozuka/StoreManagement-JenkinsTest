package inc.roms.rcs.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Dimensions {
    private Integer x;
    private Integer y;
    private Integer z;

    @Transient
    public Integer max() {
        return Math.max(Math.max(x, y), z);
    }

    @Transient
    public Integer min() {
        return Math.min(Math.min(x, y), z);
    }

    @Transient
    public Double volume() {
        return (double)(x * y * z);
    }
}
