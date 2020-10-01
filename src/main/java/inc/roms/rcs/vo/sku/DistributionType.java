package inc.roms.rcs.vo.sku;

import inc.roms.rcs.vo.common.TemperatureRegime;

public enum DistributionType {
    AMBIENT, GOODS, CHILLED, FROZEN;

    public static TemperatureRegime toTempRegime(DistributionType distributionType) {
        switch (distributionType) {
            case AMBIENT:
            case GOODS:
                return TemperatureRegime.AMBIENT;
            case FROZEN:
            case CHILLED:
                return TemperatureRegime.CHILL;
            default:
                return TemperatureRegime.ANY;
        }
    }
}
