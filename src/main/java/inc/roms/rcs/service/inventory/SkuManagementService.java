package inc.roms.rcs.service.inventory;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvException;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.request.AddSkuRequest;
import inc.roms.rcs.service.inventory.request.ImportSkusRequest;
import inc.roms.rcs.service.inventory.response.ImportSkuDetails;
import inc.roms.rcs.service.inventory.response.ImportSkusResponse;
import inc.roms.rcs.service.mujin.MujinService;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.validation.RequestNotValidException;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.sku.SkuStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkuManagementService {

    private final SkuService skuService;
    private final OmniChannelService omniChannelService;
    private final MujinService mujinService;

    @SuppressWarnings({"unchecked assignement", "rawtypes"})
    public ImportSkusResponse importSkus(ImportSkusRequest importSkusRequest) {
        try (Reader reader = new BufferedReader(new InputStreamReader(importSkusRequest.getCsvInputStream()))) {
            log.debug("Starting of sku import");

            CsvToBean<AddSkuRequest> CSVReader = new CsvToBeanBuilder(reader)
                    .withVerifier(new AddSkuRequestVerifier())
                            .withType(AddSkuRequest.class)
                            .withThrowExceptions(false)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();

            List<AddSkuRequest> skuRequests = CSVReader.parse();
            List<CsvException> capturedExceptions = CSVReader.getCapturedExceptions();

            log.debug("Csv input parsed, success for: {} lines, exceptions for: {} lines", skuRequests.size(), capturedExceptions.size());

            skuRequests.forEach(skuService::add);

            log.debug("Saved {} to db", skuRequests.size());
            return ImportSkusResponse.builder()
                    .responseCode(capturedExceptions.size() == 0 ? ResponseCode.ACCEPTED : ResponseCode.REJECTED)
                    .importDetails(ImportSkuDetails.builder()
                            .successfulImports(skuRequests.size())
                            .exceptions(capturedExceptions)
                            .build()).build();
        } catch (Exception ex) {
            throw new RuntimeException((ex));
        }
    }

    public void checkSkuTechnicalData() {
        List<Sku> allInPreparationStatus = skuService.findAllByStatus(SkuStatus.PREPARATION);

        allInPreparationStatus.stream()
                .filter(it -> mujinService.isRegistered(it.getSkuId()))
                .forEach(this::updateSkuStatus);
    }

    private void updateSkuStatus(Sku sku) {
        try {
            sku.setStatus(SkuStatus.READY);
            omniChannelService.skuUpdated(sku);
            skuService.save(sku);
        } catch (Exception ex) {
            log.error("Couldn't update sku!", ex);
        }
    }

    public void add(AddSkuRequest addSkuRequest) {
        skuService.add(addSkuRequest);
    }

    private static class AddSkuRequestVerifier implements com.opencsv.bean.BeanVerifier<AddSkuRequest> {
        @Override
        public boolean verifyBean(AddSkuRequest request) throws CsvConstraintViolationException {
            try {
                ValidationResult.Builder builder = ValidationResult.builder();
                builder.addResults(request.getSkuId().validate().getProblems());
                builder.addResults(request.getImageUrl().validate().getProblems());
                builder.addResults(request.getName().validate().getProblems());
                builder.addResults(request.getExternalId().validate().getProblems());
                builder.build().throwIfNotValid();
            } catch (RequestNotValidException rnve) {
                throw new CsvConstraintViolationException(request, rnve.getMessage());
            }
            return true;
        }
    }
}
