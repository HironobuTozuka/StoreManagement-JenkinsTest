package inc.roms.rcs.service.filter.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import inc.roms.rcs.service.filter.model.Filter;
import inc.roms.rcs.vo.filter.FilterFields;
import inc.roms.rcs.vo.filter.FilterId;
import lombok.Data;

//FIXME change the name to FilterDO (Domain Object?)
// need to be distinguished somehow from Filter (the entity)
@Data
public class FilterDetails {

    protected FilterId filterId;

    protected FilterTarget target;

    @JsonRawValue
    @JsonDeserialize(using = FilterFieldsJsonToStringDeserializer.class)
    protected FilterFields fields;

    public static FilterDetails convert(Filter filter) {
        FilterDetails filterDetails = new FilterDetails();
        filterDetails.setFields(filter.getFields());
        filterDetails.setFilterId(filter.getFilterId());
        filterDetails.setTarget(filter.getTarget());
        return filterDetails;
    }

}
