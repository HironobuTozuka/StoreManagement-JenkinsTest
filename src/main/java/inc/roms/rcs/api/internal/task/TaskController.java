package inc.roms.rcs.api.internal.task;

import inc.roms.rcs.service.task.TaskService;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Slf4j
public class TaskController {

    private TaskService taskService;

    @PostMapping("/api/internal/task:update")
    public void update(@RequestBody TaskUpdateRequest taskUpdateRequest) {
        log.info("TaskUpdateRequest: {}", taskUpdateRequest);
        taskService.handleTaskUpdate(taskUpdateRequest);
    }
}
