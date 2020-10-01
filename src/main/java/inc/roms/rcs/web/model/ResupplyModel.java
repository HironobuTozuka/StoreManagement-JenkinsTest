package inc.roms.rcs.web.model;

import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResupplyModel {
    private InductRequest inductRequest;
    private Integer numberOfTotesLeft;
}
