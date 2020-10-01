package inc.roms.rcs.web;

import inc.roms.rcs.service.inventory.domain.repository.SkuBatchRepository;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.domain.model.*;
import inc.roms.rcs.service.omnichannel.v1.OmniChannel3EClient;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.service.task.TaskService;
import inc.roms.rcs.service.task.domain.model.*;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.web.model.InvItem;
import inc.roms.rcs.web.model.Inventarization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InventarizationController {

    private final SkuService skuService;
    private final SkuBatchRepository skuBatchRepository;
    private final TaskBundleService taskBundleService;
    private final OmniChannel3EClient client;

    private final TaskService taskService;

    @GetMapping("/inventory/send")
    public void inventoryCheck() {
        List<TaskBundle> taskBundles = taskBundleService.findNotFinished();

        try {
            taskBundles.forEach(it -> {
                List<Task> tasks = it.getTasks();
                for (int i = 0; i < tasks.size(); i++) {
                    TaskUpdateRequest request = createTaskFail(tasks.get(i));
                    taskService.updateTaskOnly(request);
                }
            });
        } catch (Exception ex) {
            log.error("Error during tasks cleanup!", ex);
        }

        List<Sku> all = skuService.findAll();
        all.forEach(sku -> {
            Quantity total = skuBatchRepository.findAllBySkuId(sku.getSkuId())
                    .stream().map(SkuBatch::getQuantity).reduce(Quantity.of(0), Quantity::plus);

            Inventarization inventarization = new Inventarization();
            InvItem invItem = new InvItem();
            invItem.setId(sku.getExternalId());
            invItem.setQty(total);
            inventarization.setItems(List.of(invItem));
            client.send(inventarization);
        });
    }

    private TaskUpdateRequest createTaskFail(Task task) {
        if(task instanceof Pick) {
            Pick pick = (Pick)task;
            TaskUpdateRequest request = new TaskUpdateRequest();
            request.setTaskId(task.getTaskId().getTaskId());
            TaskDetails details = new TaskDetails();
            details.setFailed(pick.getQuantity());
            details.setPicked(Quantity.of(0));
            request.setDetails(details);
            request.setTaskStatus(TaskStatus.FAILED);
            return request;
        } else if (task instanceof Move) {
            Move move = (Move)task;
            TaskUpdateRequest request = new TaskUpdateRequest();
            request.setTaskId(task.getTaskId().getTaskId());
            request.setTaskStatus(TaskStatus.FAILED);
            return request;
        } else if (task instanceof Delivery) {
            Delivery delivery = (Delivery)task;
            TaskUpdateRequest request = new TaskUpdateRequest();
            request.setTaskStatus(TaskStatus.FAILED);
            request.setTaskId(task.getTaskId().getTaskId());
            return request;
        }
        return null;
    }

}
