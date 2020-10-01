package inc.roms.rcs.service.filter.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import inc.roms.rcs.service.filter.domain.FilterTarget;
import inc.roms.rcs.vo.filter.FilterFields;
import inc.roms.rcs.vo.filter.FilterId;
import lombok.Data;

@Entity
@Data
public class Filter {

    @EmbeddedId
    private FilterId filterId;

    @Enumerated(EnumType.STRING)
    private FilterTarget target;

    private FilterFields fields;
}
