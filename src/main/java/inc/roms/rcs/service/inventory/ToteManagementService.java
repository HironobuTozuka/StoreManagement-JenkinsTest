package inc.roms.rcs.service.inventory;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.order.domain.OrderProgressService;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToteManagementService {

    private final ToteService toteService;
    private final OrderProgressService orderProgressService;

    public void clean(ToteRequest toteRequest){
        Tote tote = toteService.findToteByToteId(toteRequest.getToteId()).orElseThrow(() -> new ToteNotFoundException(toteRequest.getToteId()));
        tote.getSlots().stream().filter(it -> it.getDeliveryInventory() != null).forEach(it -> {
            orderProgressService.removed(it.getDeliveryInventory().getOrderId());
        });
        toteService.clean(toteRequest.getToteId());
    }

}
