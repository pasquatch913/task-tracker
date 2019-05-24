package tracker.task.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import tracker.task.TaskDTO;
import tracker.task.subscription.TaskSubscriptionDTO;
import tracker.task.subscription.TaskSubscriptionEntity;

@Mapper
@DecoratedWith(TaskMapperDecorator.class)
public interface TaskMapper {

    @Mappings({
    @Mapping(target = "dueDate", ignore = true),
    @Mapping(target = "completions", ignore = true)})
    TaskDTO taskSubscriptionEntityToTaskDTO(TaskSubscriptionEntity taskSubscription);

    TaskSubscriptionEntity taskSubscriptionDTOToTaskSubscriptionEntity(TaskSubscriptionDTO taskSubscriptionDTO);
}
