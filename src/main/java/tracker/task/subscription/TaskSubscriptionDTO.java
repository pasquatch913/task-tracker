package tracker.task.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubscriptionDTO {

    private Integer id;
    private String name;
    private Integer necessaryCompletions;
    private Integer weight;
    private TaskPeriod period;
    // should be done server side really
    private Boolean active = true;

}
