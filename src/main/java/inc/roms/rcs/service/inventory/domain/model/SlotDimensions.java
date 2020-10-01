package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.Dimensions;
import inc.roms.rcs.vo.tote.ToteType;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
public class SlotDimensions {

    @EmbeddedId
    private ToteType toteType;

    private Dimensions dimensions;
}
