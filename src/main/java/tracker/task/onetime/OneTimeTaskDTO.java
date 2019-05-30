package tracker.task.onetime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneTimeTaskDTO {

    private Integer Id;
    private String name;
    private Integer weight;
    private Integer necessaryCompletions;
    private Integer completions;
    private LocalDate dueDate;
    private Boolean active;

}
