package inc.roms.rcs.service.issue;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Data;

@Data
public class CreateIssueResponse {

    private final ResponseCode responseCode;
    private final CreateIssueResponseDetails details;

}
