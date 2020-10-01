package inc.roms.rcs.service.task.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inc.roms.rcs.vo.task.TaskId;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    private TaskId taskId;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private FailReason failReason;

}
