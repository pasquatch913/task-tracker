package tracker.task.subscription;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tracker.task.analytics.TaskCompletionEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "task_instance")
public class TaskInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @NonNull
    @Column(columnDefinition = "int default 0")
    private Integer completions = 0;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationTime = LocalDateTime.now();
    @NonNull
    private LocalDate dueAt;
    @OneToMany(cascade = {CascadeType.ALL})
    private List<TaskCompletionEntity> taskCompletions = new ArrayList<TaskCompletionEntity>();

    public TaskInstanceEntity(LocalDate dueAt) {
        this.dueAt = dueAt;
        this.completions = 0;
    }
}
