package tracker;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task_instance")
public class TaskInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @NonNull
    @Column(columnDefinition = "int default 0")
    private Integer completions;
    //this doesn't work now
//    @CreationTimestamp
//    private LocalDateTime createdAt;
    @NonNull
    private LocalDate dueAt;

    public TaskInstance (LocalDate dueAt) {
        this.dueAt = dueAt;
        this.completions = 0;
    }
}
