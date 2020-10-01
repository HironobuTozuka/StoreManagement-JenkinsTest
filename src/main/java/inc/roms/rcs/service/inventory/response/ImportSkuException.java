package inc.roms.rcs.service.inventory.response;

import lombok.Data;

@Data
public class ImportSkuException {

    private final Long lineNumber;
    private final String message;

}
