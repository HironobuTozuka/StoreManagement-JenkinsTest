package inc.roms.rcs.service.task.listeners;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.task.domain.model.Delivery;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static inc.roms.rcs.service.task.domain.model.TaskStatus.COMPLETED;
import static inc.roms.rcs.service.task.domain.model.TaskStatus.FAILED;

@RequiredArgsConstructor
@Component
public class DeliveryUpdateListener extends TaskUpdateListener<Delivery> {

    private final ToteService toteService;
    private final IssueFactory issueFactory;
    private final IssueService issueService;

    @Override
    protected Class<Delivery> classOfInterest() {
        return Delivery.class;
    }

    @Override
    protected void onTaskUpdate(Delivery task, TaskUpdateRequest taskUpdateRequest) {
        if(COMPLETED.equals(task.getStatus())) {
            if(task.getOrderId()==null) return;
            toteService.cleanDeliveryTote(task.getToteId(), task.getOrderId());
        } else if (FAILED.equals(task.getStatus())) {
            CreateIssueRequest issue = issueFactory.orderNotCollected(task.getOrderId());
            issueService.createAndReport(issue);
        }
    }

}
