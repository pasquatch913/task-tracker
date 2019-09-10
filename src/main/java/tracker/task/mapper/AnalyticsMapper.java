package tracker.task.mapper;

import tracker.task.analytics.TaskDataPointDTO;
import tracker.task.analytics.TaskDataPointEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class AnalyticsMapper {

    public List<TaskDataPointDTO> taskDataPointEntityToTaskDataPointDTO(List<TaskDataPointEntity> entity) {
        return entity.stream()
                .collect(groupingBy(n -> toDate(n.getTime()),
                        summingInt(TaskDataPointEntity::getWeight)))
                .entrySet().stream()
                .map((Map.Entry<LocalDate, Integer> element)
                        -> new TaskDataPointDTO(element.getKey(), element.getValue()))
                .sorted(TaskDataPointDTO::compareTo)
                .collect(Collectors.toList());
    }

    private LocalDate toDate(LocalDateTime time) {
        return time.toLocalDate();
    }
}
