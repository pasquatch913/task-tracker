package mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import tracker.entity.TaskDTO;
import tracker.entity.TaskSubscriptionEntity;

@Mapper
@DecoratedWith(TaskMapperDecorator.class)
public interface TaskMapper {

    @Mappings({
    @Mapping(target = "dueDate", ignore = true),
    @Mapping(target = "completions", ignore = true)})
//    @Mapping(target = "dueDate", expression = "java(taskSubscription.taskInstances.get(taskSubscription.taskInstances.size()).getDueAt())")
    TaskDTO taskSubscriptionEntityToTaskDTO(TaskSubscriptionEntity taskSubscription);
}
