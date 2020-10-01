package inc.roms.rcs.builders;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteFunction;
import inc.roms.rcs.vo.tote.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static inc.roms.rcs.vo.tote.ToteHeight.LOW;
import static inc.roms.rcs.vo.tote.ToteOrientation.NORMAL;
import static inc.roms.rcs.vo.tote.TotePartitioning.BIPARTITE;
import static inc.roms.rcs.vo.tote.ToteStatus.AVAILABLE;

public class ToteBuilder {
    private ToteId toteId;
    private List<StorageSlotBuilder> storageSlotBuilders = new ArrayList<>();
    private List<DeliverySlotBuilder> deliverySlotBuilders = new ArrayList<>();
    private ToteHeight height;
    private TotePartitioning partitioning;
    private ToteOrientation orientation;
    private ToteStatus status;
    private ToteFunction toteFunction;

    private static Random random = new Random();

    public static ToteBuilder defaultTote() {
        return ToteBuilder.tote()
                .height(LOW)
                .partitioning(BIPARTITE)
                .orientation(NORMAL)
                .status(AVAILABLE);
    }

    public static ToteBuilder storageTote() {
        return ToteBuilder.tote()
                .height(LOW)
                .partitioning(BIPARTITE)
                .orientation(NORMAL)
                .toteFunction(ToteFunction.STORAGE)
                .status(AVAILABLE);
    }

    public static ToteBuilder deliveryTote() {
        return ToteBuilder.tote()
                .height(LOW)
                .partitioning(BIPARTITE)
                .orientation(NORMAL)
                .toteFunction(ToteFunction.DELIVERY)
                .status(AVAILABLE);
    }

    public static ToteBuilder randomTote() {
        return ToteBuilder.tote()
                .toteId(randomId())
                .height(LOW)
                .partitioning(BIPARTITE)
                .orientation(NORMAL)
                .status(AVAILABLE);
    }

    private static ToteId randomId() {
        return ToteId.from(String.format("%08d", random.nextInt(9999999)));
    }

    public static ToteBuilder tote() {
        return new ToteBuilder();
    }

    public ToteBuilder toteId(ToteId toteId) {
        this.toteId = toteId;
        return this;
    }

    public ToteBuilder toteFunction(ToteFunction toteFunction) {
        this.toteFunction = toteFunction;
        return this;
    }

    public Tote build() {
        if(!storageSlotBuilders.isEmpty() && !deliverySlotBuilders.isEmpty()) {
            throw new IllegalStateException();
        }
        Tote tote = new Tote();
        tote.setToteId(toteId);
        tote.setToteType(new ToteType(partitioning, height));
        tote.setToteOrientation(orientation);
        tote.setToteStatus(status);
        tote.setToteFunction(toteFunction);

        if(!storageSlotBuilders.isEmpty())
            tote.setSlots(storageSlotBuilders.stream().map(StorageSlotBuilder::build).collect(Collectors.toList()));
        if(!deliverySlotBuilders.isEmpty())
            tote.setSlots(deliverySlotBuilders.stream().map(DeliverySlotBuilder::build).collect(Collectors.toList()));

        return tote;
    }

    public ToteBuilder height(ToteHeight height) {
        this.height = height;
        return this;
    }

    public ToteBuilder partitioning(TotePartitioning partitioning) {
        this.partitioning = partitioning;
        return this;
    }

    public ToteBuilder orientation(ToteOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public ToteBuilder status(ToteStatus status) {
        this.status = status;
        return this;
    }

    public ToteBuilder slots(StorageSlotBuilder... slots) {
        storageSlotBuilders.addAll(Arrays.asList(slots));
        return this;
    }

    public ToteBuilder slots(DeliverySlotBuilder... slots) {
        deliverySlotBuilders.addAll(Arrays.asList(slots));
        return this;
    }
}
