package tracker.task.mapper;

import tracker.task.TaskDTO;
import tracker.task.TaskSubscriptionEntity;

public abstract class TaskMapperDecorator implements TaskMapper {

    private final TaskMapper mapper;

    public TaskMapperDecorator(TaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TaskDTO taskSubscriptionEntityToTaskDTO(TaskSubscriptionEntity taskSubscription) {
        TaskDTO taskDTO = mapper.taskSubscriptionEntityToTaskDTO(taskSubscription);
        taskDTO.setDueDate(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getDueAt());
        taskDTO.setCompletions(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getCompletions());
        taskDTO.setTaskInstanceId(taskSubscription.getTaskInstances()
                .get(taskSubscription.getTaskInstances().size() - 1)
                .getId());
        return taskDTO;
    }

}
