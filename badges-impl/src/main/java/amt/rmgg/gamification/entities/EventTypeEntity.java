package amt.rmgg.gamification.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Data
public class EventTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private int initialValue;

    @ManyToMany
    private List<EventCountEntity> counters;

/*    @ManyToOne
    private ApplicationEntity application;*/

}
