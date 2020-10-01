package inc.roms.rcs.service.machineoperator.model;

import inc.roms.rcs.vo.task.TaskBundleId;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExecuteTaskBundleRequest {

    private TaskBundleId taskBundleId;
    private List<TaskBase> tasks = new ArrayList<>();

    public void addAll(List<TaskBase> taskBase) {
        tasks.addAll(taskBase);
    }
}
