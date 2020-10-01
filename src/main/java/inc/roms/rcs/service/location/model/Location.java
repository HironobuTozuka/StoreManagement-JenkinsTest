package inc.roms.rcs.service.location.model;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.vo.location.LocationId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private LocationId locationId;

    @OneToOne
    private Tote currentTote;
}
