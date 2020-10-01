package inc.roms.rcs.vo.common;

import lombok.Data;

/**
 * 
 * musi byc w przyszlosci paginacja bo moze byc kilka tys sku.
 * resonse.meta.put("result_size", _result.size()); <br/>
 * resonse.meta.put("search_param_1", value_1); <br/>
 * resonse.meta.put("search_param_2", value_2); <br/>
 * resonse.meta.put("page_size", value_2); <br/>
 * 
 */
@Data
public class ListResponseMetaDetails {
    private final Integer length;
}
