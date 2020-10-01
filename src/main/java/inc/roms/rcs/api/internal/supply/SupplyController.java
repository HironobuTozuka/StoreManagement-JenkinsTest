package inc.roms.rcs.api.internal.supply;

import inc.roms.rcs.service.inventory.SupplyService;
import inc.roms.rcs.service.inventory.request.ListSupplyRequest;
import inc.roms.rcs.service.inventory.response.ListSupplyItemsRequest;
import inc.roms.rcs.service.inventory.response.ListSupplyItemsResponse;
import inc.roms.rcs.service.inventory.response.ListSupplyResponse;
import inc.roms.rcs.service.operatorpanel.request.SupplyToteRequest;
import inc.roms.rcs.service.operatorpanel.response.SupplyToteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @PostMapping("/api/internal/supply:request_tote")
    public SupplyToteResponse requestTote(@RequestBody SupplyToteRequest request) {
        return supplyService.requestTote(request);
    }

    @GetMapping("/api/internal/supply:list")
    public ListSupplyResponse list(ListSupplyRequest request) {
        return supplyService.list(request);
    }

    @GetMapping("/api/internal/supply_items:list")
    public ListSupplyItemsResponse list(ListSupplyItemsRequest request) {
        return supplyService.list(request);
    }

}
