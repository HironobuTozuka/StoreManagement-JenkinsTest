package inc.roms.rcs.vo.common;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import inc.roms.rcs.vo.types.StringVO;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
@ToString
public class TransactionId implements StringVO {

    @JsonValue
    private String transactionId;

    @JsonCreator
    public static TransactionId from(String source) {
        return new TransactionId(source);
    }

    public static TransactionId generate() {
        return TransactionId.from(UUID.randomUUID().toString());
    }

    @Transient
    public boolean isEmpty() {
        return Strings.isEmpty(transactionId);
    }

    @Override
    public String value() {
        return transactionId;
    }
}
