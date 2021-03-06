package tracker.task.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_completion_entity")
public class TaskCompletionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @Column(name = "completion_time")
    private LocalDateTime completionTime;
    @Column(name = "planned_completion")
    private LocalDateTime plannedCompletion;

    public TaskCompletionEntity(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

}
