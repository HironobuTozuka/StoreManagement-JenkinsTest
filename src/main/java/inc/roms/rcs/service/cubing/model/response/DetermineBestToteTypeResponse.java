package inc.roms.rcs.service.cubing.model.response;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.tote.ToteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetermineBestToteTypeResponse {

    private ToteType bestToteType;
    private Quantity maxQuantity;

}
