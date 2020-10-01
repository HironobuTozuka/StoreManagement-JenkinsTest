package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickRequest extends TaskBase {

    public PickRequest() {}

    private ToteData sourceTote;
    private ToteData destTote;
    private SkuId productBarcode;
    private Quantity quantity;

}
