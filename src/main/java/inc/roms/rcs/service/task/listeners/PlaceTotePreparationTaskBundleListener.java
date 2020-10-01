package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.service.task.domain.model.TaskBundleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static inc.roms.rcs.service.task.domain.model.TaskBundleType.DELIVERY;
import static inc.roms.rcs.service.task.domain.model.TaskBundleType.PICKING;

@Component
@RequiredArgsConstructor
public class PlaceTotePreparationTaskBundleListener implements TaskBundleListener {

    private final TaskBundleService taskBundleService;
    private final MachineOperatorService machineOperatorService;

    @Override
    public void onUpdate(TaskBundle taskBundle) {
        if (isFinishedPickBundle(taskBundle)) {
                machineOperatorService.prepareNewToteForDelivery();
        }
    }

    private static boolean isFinishedPickBundle(TaskBundle taskBundle) {
        return taskBundle.getType().equals(PICKING) &&
                (taskBundle.getStatus().equals(TaskBundleStatus.FAILED)
                        || taskBundle.getStatus().equals(TaskBundleStatus.COMPLETED));
    }
}
