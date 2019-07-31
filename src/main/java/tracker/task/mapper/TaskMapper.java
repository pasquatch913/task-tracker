package tracker.task.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import tracker.task.TaskDTO;
import tracker.task.TaskInstanceDTO;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskInstanceEntity;
import tracker.task.subscription.TaskSubscriptionDTO;
import tracker.task.subscription.TaskSubscriptionEntity;

@Mapper
@DecoratedWith(TaskMapperDecorator.class)
public interface TaskMapper {

    @Mappings({
    @Mapping(target = "dueDate", ignore = true),
    @Mapping(target = "completions", ignore = true)})
    TaskInstanceDTO taskSubscriptionEntityToTaskInstanceDTO(TaskSubscriptionEntity taskSubscription);

    TaskSubscriptionEntity taskSubscriptionDTOToTaskSubscriptionEntity(TaskSubscriptionDTO taskSubscriptionDTO);

    TaskSubscriptionDTO taskSubscriptionEntityToTaskSubscriptionDTO(TaskSubscriptionEntity taskSubscriptionEntity);

    OneTimeTaskDTO oneTimeTaskInstanceEntityToOneTimeTaskDTO(OneTimeTaskInstanceEntity oneTimeTaskInstanceEntity);

    OneTimeTaskInstanceEntity oneTimeTaskDTOToOneTimeTaskInstanceEntity(OneTimeTaskDTO oneTimeTaskDTO);

    // same data type for subscriptions and instances
    @Mapping(source = "taskInstanceId", target = "id")
    @Mapping(target = "recurring", constant = "true")
    TaskDTO taskInstanceDTOToTaskDTO(TaskInstanceDTO instanceDTO);

    @Mapping(target = "recurring", constant = "false")
    TaskDTO oneTimeTaskDTOToTaskDTO(OneTimeTaskDTO oneTimeTaskDTO);

}
