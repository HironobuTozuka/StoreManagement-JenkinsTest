package inc.roms.rcs.vo.tote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TotePartitioning {

    BIPARTITE(2),
    TRIPARTITE( 3),
    UNKNOWN(2);

    private final Integer numberOfSlots;
}
