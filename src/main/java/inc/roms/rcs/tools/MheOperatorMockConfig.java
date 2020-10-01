package inc.roms.rcs.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MheOperatorMockConfig {

    private static final String DELIVER = "DELIVER";
    private static final String PICK = "PICK";
    private static final String MOVE = "MOVE";
    
    @JsonProperty("task_delays")
    private Map<String, Long> taskDelays;

    public Long getTaskDelay(String type) {
        return taskDelays.get(type);
    }

    public void validate() {
        for (String pName : new String[] { MOVE, PICK, DELIVER }) {

            Long pValue = taskDelays.get(pName);
            Objects.requireNonNull(pValue, "Property '" + pName + "' must not be null.");
            if (!isInRange(pValue))
                throw new IllegalArgumentException("Property '" + pName + "' must be in range of {0...30000}.");
        }
    }

    private boolean isInRange(Long property) {
        if (property < 0l || property > 30000l)
            return false;
        return true;
    }

    public static MheOperatorMockConfig getDefault() {

        MheOperatorMockConfig config = new MheOperatorMockConfig();
        config.taskDelays = new HashMap<>();
        // Magic values taken from inc.roms.rcs.service.machineoperator.model.TaskBase
        config.taskDelays.put(MOVE, 100l);
        config.taskDelays.put(PICK, 100l);
        config.taskDelays.put(DELIVER, 100l);

        return config;
    }

}
