package tracker.task.onetime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneTimeTaskDTO {

    private Integer Id;
    private String name;
    private Integer weight;
    private Integer necessaryCompletions = 1;
    private Integer completions = 0;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Boolean active = true;

}
