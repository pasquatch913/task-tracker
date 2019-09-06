package tracker.task.mapper;

import tracker.task.analytics.TaskCompletionEntity;
import tracker.task.analytics.TaskDataPointDTO;
import tracker.task.subscription.TaskSubscriptionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyticsMapper {

    public List<TaskDataPointDTO> subscriptionToDataPoints(TaskSubscriptionEntity subscription) {
        List<TaskDataPointDTO> dataList = new ArrayList<>();

        dataList.addAll(subscription.getTaskInstances().stream()
                .flatMap(n -> n.getTaskCompletions().stream()
                        .map(TaskCompletionEntity::getCompletionTime)
                )
                .map(m -> new TaskDataPointDTO(subscription.getName(), m, subscription.getWeight()))
                .collect(Collectors.toList()));

        return dataList;
    }
}
