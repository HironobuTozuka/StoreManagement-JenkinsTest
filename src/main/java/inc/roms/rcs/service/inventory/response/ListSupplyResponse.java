package inc.roms.rcs.service.inventory.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ListSupplyResponse {

    List<SupplyDetails> supply;

}
