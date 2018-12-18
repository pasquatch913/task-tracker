package mapper;

import tracker.entity.TaskDTO;
import tracker.entity.TaskSubscriptionDTO;
import tracker.entity.TaskSubscriptionEntity;

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

    @Override
    public TaskSubscriptionEntity taskSubscriptionDTOToTaskSubscriptionEntity(TaskSubscriptionDTO taskSubscriptionDTO) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(taskSubscriptionDTO);
        taskSubscriptionEntity.setPeriod(taskSubscriptionDTO.getPeriod().getDays());
        return taskSubscriptionEntity;
    }
}
