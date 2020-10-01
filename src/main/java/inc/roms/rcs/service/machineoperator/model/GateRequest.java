package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateRequest {

    private ZoneId gateId;
    private TransactionId transactionId;

}
