package inc.roms.rcs.api.external.v1_0;

import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.roms.rcs.validation.RequestNotValidException;
import inc.roms.rcs.validation.Validator;
import inc.roms.rcs.service.filter.FilterService;
import inc.roms.rcs.service.filter.domain.FilterTarget;
import inc.roms.rcs.service.filter.request.FilterCRUDRequest;
import inc.roms.rcs.service.filter.response.FilterListResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;
    private final Validator validator;

    @PostMapping("/api/1.0/filter:create")
    public void create(@RequestBody FilterCRUDRequest createRequest) {
        validator.validate(createRequest);
        filterService.create(createRequest);
    }

    @PostMapping("/api/1.0/filter:update")
    public void update(@RequestBody FilterCRUDRequest updateRequest) {
        validator.validate(updateRequest);
        filterService.update(updateRequest);
    }

    @PostMapping("/api/1.0/filter:delete")
    public void delete(@RequestBody FilterCRUDRequest updateRequest) {
        if (Objects.isNull(updateRequest.getFilterId())) {
            throw new RequestNotValidException("filter_id must not be empty");
        }
        filterService.delete(updateRequest.getFilterId());
    }

    @GetMapping("/api/1.0/filter:list")
    public FilterListResponse list(@RequestParam("target") FilterTarget target) {
        return filterService.list(target);
    }
}
