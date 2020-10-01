package inc.roms.rcs.tools;

import inc.roms.rcs.service.machineoperator.model.ZoneState;
import inc.roms.rcs.service.machineoperator.model.ZonesStatus;
import inc.roms.rcs.service.task.domain.model.FailReason;
import inc.roms.rcs.vo.zones.ZoneFunction;
import inc.roms.rcs.vo.zones.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inc.roms.rcs.api.internal.task.TaskController;
import inc.roms.rcs.service.task.domain.model.TaskDetails;
import inc.roms.rcs.service.task.domain.model.TaskStatus;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static inc.roms.rcs.vo.common.TemperatureRegime.*;


/**
 * To use this class change inc.roms.machineoperator.url to ie.:
 * "http://localhost:8080/mheo-mock" url: "http://localhost:8080/mheo-mock"
 *
 * @author lukasz
 */
@Slf4j
@RestController
@RequestMapping("mheo-mock/api/internal")
public class MheOperatorMockController {

    private final TaskController taskController;
    private final ExecutorService executorService;
    private volatile MheOperatorMockConfig configuration;

    private volatile boolean shouldFailNextTask = false;

    @Autowired
    public MheOperatorMockController(TaskController taskController) {

        this.taskController = taskController;

        executorService = Executors.newSingleThreadExecutor();

        configuration = MheOperatorMockConfig.getDefault();
    }

    /*
     * -------- configuration
     */
    @GetMapping(value = {"/configuration:list"})
    public MheOperatorMockConfig configurationGet() {
        return configuration;
    }

    @PostMapping(value = {"/configuration:failtask"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> failNextPick() {
        this.shouldFailNextTask = true;
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = {"/configuration:update"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> configurationUpdate(@RequestBody MheOperatorMockConfig configuration) {
        try {
            configuration.validate();
        } catch (Exception e) {
            log.error("Validation error.", e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.configuration = configuration;
        return ResponseEntity.ok().build();
    }

    /*
     * -------- action
     */
    @PostMapping(value = {"/action:led", "/action:open", "/action:close"})
    @ResponseStatus(HttpStatus.OK)
    public void actionEndpoint(@RequestBody(required = false) Object request) {
    }

    /*
     * -------- task
     */
    @PostMapping(value = {"task-bundle:execute"})
    @ResponseStatus(HttpStatus.OK)
    public void taskBundleExecute(@RequestBody HashMap<String, Object> taskBundle) {
        handleTaskBundle(taskBundle);
    }

    /*
     * -------- task
     */
    @PostMapping(value = {"task-bundle:update"})
    @ResponseStatus(HttpStatus.OK)
    public void taskBundleUpdate(@RequestBody HashMap<String, Object> taskBundle) {
        handleTaskBundle(taskBundle);
    }

    private void handleTaskBundle(@RequestBody HashMap<String, Object> taskBundle) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) taskBundle.get("tasks");
        Objects.requireNonNull(tasks, "Field 'tasks' must not be null.");

        if (shouldFailNextTask) {
            shouldFailNextTask = false;
            failOneTask(tasks);
        } else {
            executeTasks(tasks);
        }
    }

    private void executeTasks(List<Map<String, Object>> tasks) {
        for (Map<String, Object> task : tasks) {
            String taskId = (String) task.get("task_id");
            String taskType = (String) task.get("type");

            scheduleResponse(taskId, taskType,
                    new TaskUpdateRequest(
                            taskId,
                            TaskStatus.COMPLETED,
                            getSuccessTaskDetails(task)
                    ));
        }
    }

    private void failOneTask(List<Map<String, Object>> tasks) {
        Map<String, Object> task = tasks.get(0);
        String taskId = (String) task.get("task_id");
        String taskType = (String) task.get("type");
        scheduleResponse(taskId, taskType,
                new TaskUpdateRequest(
                        taskId,
                        TaskStatus.FAILED,
                        getFailureTaskDetails(task)
                ));
    }

    private void scheduleResponse(String taskId, String taskType, TaskUpdateRequest taskUpdateRequest) {
        Objects.requireNonNull(taskId, "Field 'task_id' must not be null.");

        Objects.requireNonNull(taskType, "Field 'type' must not be null.");

        Runnable runnableTask = () -> {
            try {
                Long taskDelay = configuration.getTaskDelay(taskType);
                log.info("Fake update to be sent in {}ms, taskId: {}", taskDelay, taskId);
                TimeUnit.MILLISECONDS.sleep(taskDelay);
                taskController.update(taskUpdateRequest);
                log.info("Fake update finished, taskId: {}", taskId);
            } catch (InterruptedException e) {
                log.error("Fake update failed, taskId: " + taskId, e);
            }
        };
        executorService.execute(runnableTask);
    }

    private TaskDetails getFailureTaskDetails(Map<String, Object> task) {
        TaskDetails failureDetails = null;
        if (task.get("type").equals("PICK")) {
            failureDetails = new TaskDetails();
            failureDetails.setFailed(Quantity.of((Integer) task.get("quantity")));
            failureDetails.setPicked(Quantity.of(0));
            failureDetails.setFailReason(FailReason.DEST_TOTE_ERROR);
            failureDetails.setFailDescription("");
        }
        return failureDetails;
    }

    private TaskDetails getSuccessTaskDetails(Map<String, Object> task) {
        TaskDetails successTaskDetails = null;
        if (task.get("type").equals("PICK")) {
            successTaskDetails = new TaskDetails();
            successTaskDetails.setPicked(Quantity.of((Integer) task.get("quantity")));
            successTaskDetails.setFailed(Quantity.of(0));
        }
        return successTaskDetails;
    }

    @GetMapping("zone:list")
    public ZonesStatus listZones() {
        ZonesStatus zonesStatus = new ZonesStatus();
        ArrayList<ZoneState> zones = new ArrayList<>();
        zones.add(new ZoneState(ZoneId.from("AMBIENT"), AMBIENT, List.of(ZoneFunction.STORAGE), Quantity.of(100)));
        zones.add(new ZoneState(ZoneId.from("CHILL"), CHILL, List.of(ZoneFunction.STORAGE), Quantity.of(100)));
        zones.add(new ZoneState(ZoneId.from("STAGING"), ANY, List.of(ZoneFunction.STAGING), Quantity.of(100)));
        zones.add(new ZoneState(ZoneId.from("LOADING_GATE"), ANY, List.of(ZoneFunction.LOADING_GATE), Quantity.of(100)));
        zones.add(new ZoneState(ZoneId.from("PLACE"), ANY, List.of(ZoneFunction.PLACE), Quantity.of(1)));
        zonesStatus.setZones(zones);
        return zonesStatus;
    }
}
