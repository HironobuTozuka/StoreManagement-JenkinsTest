package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.task.domain.model.TaskBundle;

public interface TaskBundleListener {

    void onUpdate(TaskBundle taskBundle);
}
