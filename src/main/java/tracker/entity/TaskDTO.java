package tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Integer id;
    private String name;
    private Integer weight;
    private Integer necessaryCompletions;
    private Integer completions;
    private LocalDate dueDate;
    private Integer taskInstanceId;
    private Boolean active;

}
