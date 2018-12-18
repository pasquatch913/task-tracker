package tracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;

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
    //this doesn't work now
//    @CreationTimestamp
//    private LocalDateTime createdAt;
    @NonNull
    private LocalDate dueAt;

    public TaskInstanceEntity(LocalDate dueAt) {
        this.dueAt = dueAt;
        this.completions = 0;
    }
}
