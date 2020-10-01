package inc.roms.rcs.service.task.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateRequest {

    private String taskId;
    private TaskStatus taskStatus;
    private TaskDetails details;

}
