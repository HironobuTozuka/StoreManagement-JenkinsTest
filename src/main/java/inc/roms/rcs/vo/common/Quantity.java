package inc.roms.rcs.vo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Quantity implements Comparable<Quantity> {

    @JsonValue
    private Integer quantity;

    @JsonCreator
    public static Quantity of(int quantity) {
        return new Quantity(quantity);
    }

    @JsonCreator
    public static Quantity of(String quantity) {
        return new Quantity(Integer.valueOf(quantity));
    }

    public static Quantity min(Quantity quantityToBeReserved, Quantity available) {
        return Quantity.of(Math.min(quantityToBeReserved.getQuantity(), available.getQuantity()));
    }

    @Transient
    public boolean gt(int other) {
        return quantity > other;
    }

    @Transient
    public boolean gt(Quantity other) {
        return quantity > other.getQuantity();
    }

    public Quantity minus(Quantity other) {
        return Quantity.of(quantity - other.getQuantity());
    }

    public Quantity multiply(Long other) {
        return Quantity.of((int) (quantity * other));
    }

    public Quantity plus(Quantity quantity2) {
        if(quantity2 == null)  return Quantity.of(quantity);
        return Quantity.of(quantity + quantity2.getQuantity());
    }

    @Override
    public int compareTo(Quantity o) {
        return quantity.compareTo(o.getQuantity());
    }
}
