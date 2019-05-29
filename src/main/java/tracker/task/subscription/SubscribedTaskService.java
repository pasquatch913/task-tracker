package tracker.task.subscription;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.TaskInstanceDTO;
import tracker.task.mapper.TaskMapper;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscribedTaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TaskSubscriptionRepository taskSubscriptionRepository;

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(task);
        UserEntity user = userService.getUser();
        user.getTaskSubscriptions().add(taskSubscriptionEntity);
        userRepository.save(user);
    }

    public List<TaskSubscriptionDTO> returnTaskSubscriptionsForUser(UserEntity user) {
        List<TaskSubscriptionEntity> tasks = user.getTaskSubscriptions();
        return tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskSubscriptionDTO)
                .collect(Collectors.toList());
    }

    public List<TaskInstanceDTO> returnTaskInstancesForUser(UserEntity user) {
        generateTaskInstances(user);
        List<TaskSubscriptionEntity> tasks = user.getTaskSubscriptions();
        return tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskInstanceDTO)
                .collect(Collectors.toList());
    }

    public void unsubscribe(Integer id) {
        TaskSubscriptionEntity taskToUpdate = taskSubscriptionRepository.findById(id).get();
        taskToUpdate.setActive(false);
        taskSubscriptionRepository.save(taskToUpdate);
    }

    public void generateTaskInstances(UserEntity user) {
        List<TaskSubscriptionEntity> subscriptions = user.getTaskSubscriptions();

        // if any subscription doesn't have an instance in the future, generate it
        subscriptions.stream().filter(this::noFutureTasks)
                .forEach(this::generateNewInstanceForPeriod);

        userRepository.save(user);
    }

    public Boolean verifyTaskInstance(UserEntity user, Integer subscriptionId, Integer instanceId) {
        // find the relevant task subscription (will only be 1 match at most)
        TaskSubscriptionEntity task = user.getTaskSubscriptions()
                .stream()
                .filter(n -> n.getId().equals(subscriptionId))
                .collect(Collectors.toList()).get(0);
        // if the current user has one match for subscription and task ID, return true
        return task.getTaskInstances().stream()
                .filter(m -> m.getId().equals(instanceId))
                .collect(Collectors.toList()).size() == 1;
    }

    public void updateTaskInstanceCompletions(Integer id, Integer value) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(value);
        taskInstanceRepository.save(taskToUpdate);
    }

    private void generateNewInstanceForPeriod(TaskSubscriptionEntity taskSubscriptionEntity) {
        LocalDate nextDueDate = dueDateForNextInstance(taskSubscriptionEntity);
        TaskInstanceEntity newTaskInstance = new TaskInstanceEntity(nextDueDate);

        taskInstanceRepository.save(newTaskInstance);
        taskSubscriptionEntity.getTaskInstances().add(newTaskInstance);
        taskSubscriptionRepository.save(taskSubscriptionEntity);
    }

    private LocalDate dueDateForNextInstance(TaskSubscriptionEntity subscriptionEntity) {
        switch (subscriptionEntity.getPeriod()) {
            case DAILY:
                return LocalDate.now();
            case WEEKLY:
                return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case MONTHLY:
                return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            default:
                return LocalDate.now();
        }
    }

    private Boolean noFutureTasks(TaskSubscriptionEntity subscription) {
        return subscription.getTaskInstances().isEmpty()
                || subscription.getTaskInstances()
                .get(subscription.getTaskInstances().size() - 1)
                .getDueAt().isBefore(LocalDate.now());
    }

}
