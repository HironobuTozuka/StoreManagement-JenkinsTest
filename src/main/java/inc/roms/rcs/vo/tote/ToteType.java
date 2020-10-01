package inc.roms.rcs.vo.tote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToteType implements Serializable {

    @Enumerated(EnumType.STRING)
    private TotePartitioning totePartitioning;

    @Enumerated(EnumType.STRING)
    private ToteHeight toteHeight;
}
