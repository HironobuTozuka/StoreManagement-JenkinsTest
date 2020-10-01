package inc.roms.rcs.service.cubing.model.request;

import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.tote.ToteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetermineBestToteTypeRequest {

    private Sku sku;
    private Set<ToteType> availableToteTypes;
    private double usableVolumeCoefficient;

}
