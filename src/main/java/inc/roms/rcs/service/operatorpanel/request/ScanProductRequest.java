package inc.roms.rcs.service.operatorpanel.request;

import inc.roms.rcs.vo.tote.TotePartitioning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScanProductRequest {
    private TotePartitioning totePartitioning;
}
