package inc.roms.rcs.service.issue.response;

import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class IssueListResponse {

    private final List<IssueDetails> issues;
    private final ListResponseMetaDetails meta;

}
