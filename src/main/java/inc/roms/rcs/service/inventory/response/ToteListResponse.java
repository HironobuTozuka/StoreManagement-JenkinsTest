package inc.roms.rcs.service.inventory.response;

import java.util.ArrayList;
import java.util.List;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import lombok.Getter;

@Getter
public class ToteListResponse {

    public List<ToteDetails> totes;
    private ListResponseMetaDetails meta;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<Tote> totes;

        public Builder totes(List<Tote> totes) {
            this.totes = totes;
            return this;
        }

        public ToteListResponse build() {

            ToteListResponse response = new ToteListResponse();
            response.totes = new ArrayList<>();
            for (Tote tote : totes) {
                response.totes.add(ToteDetails.convert(tote));
            }

            response.meta = new ListResponseMetaDetails(response.totes.size());

            return response;

        }

    }

}
