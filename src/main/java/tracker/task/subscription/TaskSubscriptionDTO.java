package tracker.task.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tracker.task.subscription.TaskPeriod;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubscriptionDTO {

    private String name;
    private Integer necessaryCompletions;
    private Integer weight;
    private TaskPeriod period;

}
