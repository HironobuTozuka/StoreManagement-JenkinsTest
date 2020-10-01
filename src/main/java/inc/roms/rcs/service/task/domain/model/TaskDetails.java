package inc.roms.rcs.service.task.domain.model;

import inc.roms.rcs.vo.common.Quantity;
import lombok.Data;

@Data
public class TaskDetails {

    private Quantity picked;

    private Quantity failed;

    private FailReason failReason;

    private String failDescription;

}
