package tracker.task.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDataPointEntity {

    private String name;
    private LocalDateTime time;
    private Integer weight;

}
