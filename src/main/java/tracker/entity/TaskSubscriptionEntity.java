package tracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
@Entity
@Table(name = "task_subscription")
public class TaskSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private Integer period;
    @NonNull
    private Integer weight;
    @NonNull
    private Integer minimumCompletions = 1;
    @OneToMany
    private List<TaskInstanceEntity> taskInstances = new ArrayList<TaskInstanceEntity>();


}
