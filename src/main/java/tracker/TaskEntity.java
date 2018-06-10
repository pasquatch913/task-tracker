package tracker;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;


@NoArgsConstructor
@Data
@Entity
@Table(name = "task_entity")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private Integer period;
    @NonNull
    private Integer weight;
    @NonNull
    private Integer userId;

    public TaskEntity(String name, Integer period, Integer weight, Integer userId) {
        this.name = name;
        this.period = period;
        this.weight = weight;
        this.userId = userId;
    }

//    public String getName() {
//        return this.name;
//    }
}
