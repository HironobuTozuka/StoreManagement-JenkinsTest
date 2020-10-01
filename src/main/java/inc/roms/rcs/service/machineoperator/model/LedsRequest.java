package inc.roms.rcs.service.machineoperator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedsRequest {

    private List<Integer> ledIds = new ArrayList<>();

}
