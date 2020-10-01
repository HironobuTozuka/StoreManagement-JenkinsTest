package inc.roms.rcs.vo.common;

import java.util.function.BinaryOperator;

public enum TemperatureRegimeSku {

    CHILLED(0), AMBIENT(10), GOODS(20), FROZEN(30);

    private final int weight;

    TemperatureRegimeSku(int weight) {
        this.weight = weight;
    }

    public static BinaryOperator<TemperatureRegimeSku> ACCUMULATOR = (tr1, tr2) -> {
        if(tr1.weight > tr2.weight) return tr2;
        return tr1;
    };
}
