package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteOrientation;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.tote.ToteType;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Data
@Entity
public class Tote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private ToteId toteId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "tote_id")
    @OrderBy("ordinal")
    private List<Slot> slots;

    private ToteType toteType;

    @Enumerated(EnumType.STRING)
    private ToteOrientation toteOrientation;

    @Enumerated(EnumType.STRING)
    private ToteStatus toteStatus;

    @Enumerated(EnumType.STRING)
    private TemperatureRegime temperatureRegime;

    private ZoneId zoneId;

    @Enumerated(EnumType.STRING)
    private ToteFunction toteFunction;

    public List<SkuBatch> skuBatches() {
        return getSlots()
                .stream()
                .filter(it -> it.getStorageInventory() != null)
                .map(Slot::getStorageInventory)
                .filter(it -> it.getSkuBatch() != null)
                .map(StorageInventory::getSkuBatch)
                .filter(Objects::nonNull).collect(toList());
    }

    @Transient
    public Long getNotEmptySlotsCount() {
        if (slots == null) {
            return 0L;
        }

        return slots.stream().filter(it -> !it.isEmpty()).count();
    }

    @Transient
    public Long getEmptySlotsCount() {
        return toteType.getTotePartitioning().getNumberOfSlots() - getNotEmptySlotsCount();
    }

    @Transient
    public List<Slot> getAllSlots() {
        if (toteType == null) return new ArrayList<>();
        return IntStream
                .range(0, toteType.getTotePartitioning().getNumberOfSlots())
                .mapToObj(this::getSlotByOrdinal)
                .peek(this::fillNonDelivery)
                .sorted(byOrdinal())
                .collect(toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tote that = (Tote) o;
        if(id != null || that.id != null)
            return Objects.equals(id, that.id);
        else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        if(id != null)
            return Objects.hash(id);
        else
            return super.hashCode();
    }

    private void fillNonDelivery(Slot slot) {
        if (slot.getDeliveryInventory() != null && slot.getDeliveryInventory().getOrderId() != null) return;

        slot.setDeliveryInventory(null);
        if (slot.getStorageInventory() == null || slot.getStorageInventory().isEmpty()) {
            StorageInventory storageInventory = new StorageInventory();
            storageInventory.setSkuBatch(new SkuBatch());
            slot.setStorageInventory(storageInventory);
        }
    }

    public Slot getSlotByOrdinal(int ordinal) {
        if (slots == null) this.slots = new ArrayList<>();
        return slots
                .stream()
                .filter(nonEmptySlot -> ordinal == nonEmptySlot.getOrdinal())
                .findFirst().orElseGet(() -> new Slot(ordinal));
    }

    private Comparator<Slot> byOrdinal() {
        return (slot1, slot2) -> {
            if (this.getToteOrientation().equals(ToteOrientation.REVERSED))
                return Integer.compare(slot2.getOrdinal(), slot1.getOrdinal());
            return Integer.compare(slot1.getOrdinal(), slot2.getOrdinal());
        };
    }

    public void addSlot(Slot slot) {
        if(this.slots.contains(slot)) return;

        this.slots.remove(getSlotByOrdinal(slot.getOrdinal()));
        this.slots.add(slot);
    }
}
