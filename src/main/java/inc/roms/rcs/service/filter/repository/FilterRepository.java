package inc.roms.rcs.service.filter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import inc.roms.rcs.service.filter.domain.FilterTarget;
import inc.roms.rcs.service.filter.model.Filter;
import inc.roms.rcs.vo.filter.FilterId;

@Repository
public interface FilterRepository extends CrudRepository<Filter, FilterId> {

    @SuppressWarnings("unchecked")
    @Override
    Filter save(Filter entity);

    @Override
    void deleteById(FilterId id);

    List<Filter> findAllByTarget(FilterTarget target);

    Filter findByFilterId(FilterId filterId);

}
