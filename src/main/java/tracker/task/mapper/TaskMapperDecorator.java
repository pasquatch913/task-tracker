package tracker.task.mapper;

import tracker.task.TaskInstanceDTO;
import tracker.task.subscription.TaskSubscriptionEntity;

public abstract class TaskMapperDecorator implements TaskMapper {

    private final TaskMapper mapper;

    public TaskMapperDecorator(TaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TaskInstanceDTO taskSubscriptionEntityToTaskInstanceDTO(TaskSubscriptionEntity taskSubscription) {
        TaskInstanceDTO taskInstanceDTO = mapper.taskSubscriptionEntityToTaskInstanceDTO(taskSubscription);
        taskInstanceDTO.setDueDate(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getDueAt());
        taskInstanceDTO.setCompletions(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getCompletions());
        taskInstanceDTO.setTaskInstanceId(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getId());
        return taskInstanceDTO;
    }

}
