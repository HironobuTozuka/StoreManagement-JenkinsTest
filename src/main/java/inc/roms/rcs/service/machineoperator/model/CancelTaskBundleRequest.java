package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.task.TaskBundleId;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CancelTaskBundleRequest {

    public CancelTaskBundleRequest(TaskBundleId taskBundleId) {
        this.taskBundleId = taskBundleId;
    }

    private TaskBundleId taskBundleId;
}
