package inc.roms.rcs.service.filter;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import inc.roms.rcs.exception.ResourceNotFoundException;
import inc.roms.rcs.service.filter.domain.FilterTarget;
import inc.roms.rcs.service.filter.model.Filter;
import inc.roms.rcs.service.filter.repository.FilterRepository;
import inc.roms.rcs.service.filter.request.FilterCRUDRequest;
import inc.roms.rcs.service.filter.response.FilterListResponse;
import inc.roms.rcs.vo.filter.FilterId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final FilterRepository filterRepository;

    public void create(FilterCRUDRequest createRequest) {
        // tutaj powinna byc validacja, ale nie ma, bo jest robiona na
        // controlerze.
        Filter entity = new Filter();
        BeanUtils.copyProperties(createRequest, entity);
        filterRepository.save(entity);

    }

    public void update(FilterCRUDRequest updateRequest) {
        Filter filter = filterRepository.findByFilterId(updateRequest.getFilterId());
        if (Objects.isNull(filter)) {
            throw new ResourceNotFoundException(updateRequest.getFilterId().toString(), Filter.class);
        }
        // tutaj powinna byc validacja, ale nie ma, bo jest robiona na
        // controlerze.
        BeanUtils.copyProperties(updateRequest, filter);
        filterRepository.save(filter);
    }

    public void delete(FilterId filterId) {
        try {
            filterRepository.deleteById(filterId);
        } catch (EmptyResultDataAccessException emptyResult) {
            throw new ResourceNotFoundException(filterId.getFilterId(), Filter.class);
        }
    }

    public FilterListResponse list(FilterTarget filterTarget) {
        List<Filter> filters = filterRepository.findAllByTarget(filterTarget);
        FilterListResponse list = FilterListResponse.builder().filters(filters).build();
        return list;
    }

}
