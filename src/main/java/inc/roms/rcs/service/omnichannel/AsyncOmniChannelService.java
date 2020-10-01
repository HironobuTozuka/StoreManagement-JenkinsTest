package inc.roms.rcs.service.omnichannel;

import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Async
@Qualifier("AsyncOmniChannelService")
public class AsyncOmniChannelService implements OmniChannelService {

    private final OmniChannelService omniChannelService;

    @Override
    public void updateInventory(Map<SkuId, Quantity> skuDiff) {
        omniChannelService.updateInventory(skuDiff);
    }

    @Override
    public void updateInventory(SkuBatch skuBatch, TaskUpdateRequest taskUpdateRequest, OrderType orderType){
        omniChannelService.updateInventory(skuBatch,taskUpdateRequest, orderType);
    }

    @Override
    public void updateInventory(ScheduledSupplyItem supplyItem, Quantity quantity) {
        omniChannelService.updateInventory(supplyItem, quantity);
    }

    @Override
    public void updateInventory(SkuId skuId, Quantity quantity) {
        omniChannelService.updateInventory(skuId, quantity);
    }

    @Override
    public void orderStatusChanged(Order order){
        omniChannelService.orderStatusChanged(order);
    }

    @Override
    public void skuUpdated(Sku it){
        omniChannelService.skuUpdated(it);
    }

    @Override
    public void removeInventory(SkuBatch skuBatch){
        omniChannelService.removeInventory(skuBatch);
    }

    @Override
    public void addInventory(SkuBatch skuBatch){
        omniChannelService.addInventory(skuBatch);
    }
}
