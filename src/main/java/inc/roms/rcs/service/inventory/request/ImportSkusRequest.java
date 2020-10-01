package inc.roms.rcs.service.inventory.request;

import lombok.Data;

import java.io.InputStream;

@Data
public class ImportSkusRequest {
    private final InputStream csvInputStream;

    public ImportSkusRequest(InputStream csvInputStream) {
        this.csvInputStream = csvInputStream;
    }
}
