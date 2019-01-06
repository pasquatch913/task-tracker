package tracker.task;

import lombok.Data;

@Data
public class TaskSubscriptionDTO {

    private String name;
    private Integer necessaryCompletions;
    private Integer weight;
    private TaskPeriod period;

}