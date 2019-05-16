package tracker.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubscriptionDTO {

    private String name;
    private Integer necessaryCompletions;
    private Integer weight;
    private TaskPeriod period;

}
