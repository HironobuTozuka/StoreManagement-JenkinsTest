package inc.roms.rcs.service.task.domain.repository;

import inc.roms.rcs.service.task.domain.model.Task;
import inc.roms.rcs.service.task.domain.model.TaskBundle;
import inc.roms.rcs.service.task.domain.model.TaskBundleStatus;
import inc.roms.rcs.vo.order.OrderId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskBundleRepository extends CrudRepository<TaskBundle, Integer> {

    List<TaskBundle> findAll();

    TaskBundle findFirstByTasksContaining(Task task);

    List<TaskBundle> findAllByStatusNotIn(List<TaskBundleStatus> completed);

    List<TaskBundle> findAllByOrderId(OrderId orderId);
}
