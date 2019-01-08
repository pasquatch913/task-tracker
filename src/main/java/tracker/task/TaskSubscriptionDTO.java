package tracker.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskSubscriptionDTO {

    private String name;
    private Integer necessaryCompletions;
    private Integer weight;
    private TaskPeriod period;

}
