package tracker.task.onetime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class OneTimeTaskInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private Integer weight;
    @NonNull
    private Integer necessaryCompletions = 0;
    @NonNull
    private Integer completions = 0;
    @NonNull
    private LocalDate dueDate;
    private Boolean active = true;

}
