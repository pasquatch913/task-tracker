package tracker.task.subscription;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tracker.task.subscription.TaskInstanceEntity;
import tracker.task.subscription.TaskPeriod;

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
    private TaskPeriod period;
    @NonNull
    private Integer weight;
    @NonNull
    private Integer necessaryCompletions = 1;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<TaskInstanceEntity> taskInstances = new ArrayList<TaskInstanceEntity>();
    @NonNull
    private Boolean active = true;

}
