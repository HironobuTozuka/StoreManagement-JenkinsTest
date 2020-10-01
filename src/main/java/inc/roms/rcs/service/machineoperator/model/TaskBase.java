package inc.roms.rcs.service.machineoperator.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import inc.roms.rcs.vo.task.TaskId;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PickRequest.class, name = "PICK"),
        @JsonSubTypes.Type(value = MoveRequest.class, name = "MOVE"),
        @JsonSubTypes.Type(value = DeliveryRequest.class, name = "DELIVER")
})
@Data
public class TaskBase {

    private TaskId taskId;

}
