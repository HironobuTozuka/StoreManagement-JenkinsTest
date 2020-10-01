package inc.roms.rcs.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Inventarization {

    private String number = UUID.randomUUID().toString();
    private List<InvItem> items;

    @JsonProperty("unlockItems")
    private boolean unlockItems = true;
}
