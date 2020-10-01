package inc.roms.rcs.service.inventory.response;

import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportSkuDetails {

    private Integer successfulImports;
    private List<ImportSkuException> exceptions;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer successfulImports;
        private List<ImportSkuException> exceptions;

        public Builder successfulImports(Integer successfulImports) {
            this.successfulImports = successfulImports;
            return this;
        }

        public Builder exceptions(List<CsvException> csvExceptions) {
            this.exceptions = csvExceptions.stream().map(it -> new ImportSkuException(it.getLineNumber(), it.getMessage())).collect(Collectors.toList());
            return this;
        }

        public ImportSkuDetails build() {
            return new ImportSkuDetails(successfulImports, exceptions);
        }
    }


}
