package inc.roms.rcs.service.task.domain.repository;

import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.vo.task.TaskId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    Task findByTaskId(TaskId taskId);

    List<Task> findAll();

}
