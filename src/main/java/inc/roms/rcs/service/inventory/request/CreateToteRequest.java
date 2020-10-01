package inc.roms.rcs.service.inventory.request;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteHeight;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.TotePartitioning;
import lombok.Data;

import java.util.List;

@Data
public class CreateToteRequest {
    private ToteId toteId;
    private TotePartitioning partitioning;
    private ToteHeight height;
    private List<SlotModel> slotModels;

    @Data
    public static class SlotModel {

        private int ordinal;
        private SkuId skuId;
        private Quantity quantity;

    }

}
