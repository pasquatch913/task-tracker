package tracker.task.mapper;

import tracker.task.TaskInstanceDTO;
import tracker.task.subscription.TaskInstanceEntity;
import tracker.task.subscription.TaskSubscriptionEntity;

import java.util.Collections;

public abstract class TaskMapperDecorator implements TaskMapper {

    private final TaskMapper mapper;

    public TaskMapperDecorator(TaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TaskInstanceDTO taskSubscriptionEntityToTaskInstanceDTO(TaskSubscriptionEntity taskSubscription) {
        TaskInstanceDTO taskInstanceDTO = mapper.taskSubscriptionEntityToTaskInstanceDTO(taskSubscription);
        TaskInstanceEntity latestTaskInstance = Collections.max(taskSubscription.getTaskInstances(), new TaskInstanceComparator());
        taskInstanceDTO.setDueDate(latestTaskInstance.getDueAt());
        taskInstanceDTO.setCompletions(latestTaskInstance.getCompletions());
        taskInstanceDTO.setId(latestTaskInstance.getId());
        return taskInstanceDTO;
    }

}
