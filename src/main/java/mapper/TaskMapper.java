package mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tracker.entity.TaskDTO;
import tracker.entity.TaskSubscriptionEntity;

@Mapper
@DecoratedWith(TaskMapperDecorator.class)
public interface TaskMapper {

    @Mapping(target = "dueDate", ignore = true)
//    @Mapping(target = "dueDate", expression = "java(taskSubscription.taskInstances.get(taskSubscription.taskInstances.size()).getDueAt())")
    TaskDTO taskSubscriptionEntityToTaskDTO(TaskSubscriptionEntity taskSubscription);
}
