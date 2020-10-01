package inc.roms.rcs.vo.common;

import java.util.function.BinaryOperator;

public enum TemperatureRegime {

    CHILL(0), AMBIENT(10), ANY(20);

    private final int weight;

    TemperatureRegime(int weight) {
        this.weight = weight;
    }

    public static BinaryOperator<TemperatureRegime> ACCUMULATOR = (tr1, tr2) -> {
        if(tr1.weight > tr2.weight) return tr2;
        return tr1;
    };
}
