package tracker;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;


@NoArgsConstructor
@Data
@Entity
@Table(name = "Tasks")
public class TaskEntity {

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private Integer period;
    @NonNull
    private Integer weight;

    public TaskEntity(String name, Integer period, Integer weight) {
        this.name = name;
        this.period = period;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }
}
