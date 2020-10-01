package inc.roms.rcs.vo.sku;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeliveryTurn {
    @JsonProperty("1")
    FIRST,
    @JsonProperty("2")
    SECOND,
    @JsonProperty("3")
    THIRD,
    @JsonProperty("4")
    FOURTH,
    @JsonProperty("5")
    FIFTH;

}
