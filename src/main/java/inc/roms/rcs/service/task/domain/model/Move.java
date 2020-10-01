package inc.roms.rcs.service.task.domain.model;

import inc.roms.rcs.vo.task.TaskId;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
public class Move extends Task {
    private ToteId toteId;

    @AttributeOverride(name = "zoneId", column = @Column(name = "destination"))
    private ZoneId destination;

    public Move(ToteId toteId, ZoneId destination) {
        this.toteId = toteId;
        this.destination = destination;
        this.setTaskId(TaskId.generate());
        this.setStatus(TaskStatus.CREATED);
    }
}
