package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteOrientation;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.tote.ToteType;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ToteDetails {

    private ToteId toteId;
    private List<SlotDetails> slots;
    private ToteType toteType;
    private ToteOrientation toteOrientation;
    private ToteStatus toteStatus;
    private ToteFunction toteFunction;

    public static ToteDetails convert(Tote tote) {
        ToteDetails details = new ToteDetails();
        details.toteFunction = tote.getToteFunction();
        details.toteId = tote.getToteId();
        details.slots = convertSlots(tote);
        details.toteType = tote.getToteType();
        details.toteOrientation = tote.getToteOrientation();
        details.toteStatus = tote.getToteStatus();
        return details;
    }

    private static List<SlotDetails> convertSlots(Tote tote) {
        return tote.getAllSlots().stream()
                .map(s -> SlotDetails.convert(tote, s))
                .collect(Collectors.toList());
    }

}
