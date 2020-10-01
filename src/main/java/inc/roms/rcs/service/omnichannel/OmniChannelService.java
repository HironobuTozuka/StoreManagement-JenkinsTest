package inc.roms.rcs.service.omnichannel;

import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;

import java.util.Map;

public interface OmniChannelService {
    void updateInventory(Map<SkuId, Quantity> skuDiff);

    void updateInventory(SkuBatch skuBatch, TaskUpdateRequest taskUpdateRequest, OrderType orderType);

    void updateInventory(ScheduledSupplyItem supplyItem, Quantity quantity);

    void updateInventory(SkuId skuId, Quantity quantity);

    void orderStatusChanged(Order order);

    void skuUpdated(Sku it);

    void removeInventory(SkuBatch skuBatch);

    void addInventory(SkuBatch skuBatch);
}
