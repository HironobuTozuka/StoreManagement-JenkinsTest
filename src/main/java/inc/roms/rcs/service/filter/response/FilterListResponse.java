package inc.roms.rcs.service.filter.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import inc.roms.rcs.service.filter.domain.FilterDetails;
import inc.roms.rcs.service.filter.model.Filter;
import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import lombok.Getter;

@Getter
public class FilterListResponse {

    private List<FilterDetails> filters;
    private ListResponseMetaDetails meta;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Filter> filters;

        public Builder filters(List<Filter> filters) {
            Objects.requireNonNull(filters, "filters must not be null");
            this.filters = filters;
            return this;
        }

        public FilterListResponse build() {
            FilterListResponse response = new FilterListResponse();
            response.filters = new ArrayList<>();

            for (Filter filter : filters) {
                response.filters.add(FilterDetails.convert(filter));
            }

            response.meta = new ListResponseMetaDetails(response.filters.size());
            return response;
        }

    }
}
