package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DeliveryRequest extends TaskBase {
    private ToteId toteId;
    private List<Integer> slots;

}
