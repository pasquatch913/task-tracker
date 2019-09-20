package tracker.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInstanceDTO {

    private Integer taskSubscriptionId;
    private String name;
    private Integer weight;
    private Integer necessaryCompletions;
    private Integer completions;
    private LocalDate dueDate;
    private Integer id;
    private Boolean active;

}
