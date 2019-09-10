package tracker.task.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDataPointDTO implements Comparable<TaskDataPointDTO> {

    //    private String name;
    private LocalDate date;
    private Integer points;

    @Override
    public int compareTo(TaskDataPointDTO other) {
        if (getDate() == null || other.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(other.getDate());
    }
}
