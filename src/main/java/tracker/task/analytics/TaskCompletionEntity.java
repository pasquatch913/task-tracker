package tracker.task.analytics;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
public class TaskCompletionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "created_at")
    private LocalDateTime completionTime = LocalDateTime.now();

}
