package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.task.domain.model.Move;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static inc.roms.rcs.service.task.domain.model.TaskStatus.COMPLETED;

@RequiredArgsConstructor
@Component
public class MoveUpdateListener extends TaskUpdateListener<Move> {

    private final ToteService toteService;

    @Override
    protected Class<Move> classOfInterest() {
        return Move.class;
    }

    @Override
    protected void onTaskUpdate(Move task, TaskUpdateRequest taskUpdateRequest) {
        if(COMPLETED.equals(task.getStatus())) {
            Optional<Tote> maybeTote = toteService.findToteByToteId(task.getToteId());
            maybeTote.ifPresent(tote -> {
                tote.setZoneId(task.getDestination());
                toteService.updateTote(tote);
            });
        }
    }
}
