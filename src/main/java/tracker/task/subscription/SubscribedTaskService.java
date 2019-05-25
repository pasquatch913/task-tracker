package tracker.task.subscription;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.mapper.TaskMapper;
import tracker.task.onetime.OneTimeTaskInstanceEntity;
import tracker.task.onetime.OneTimeTaskInstanceRepository;
import tracker.task.subscription.*;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

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

    public List<TaskSubscriptionEntity> returnTaskForUser(UserEntity user) {
        generateTaskInstances(user);
        List<TaskSubscriptionEntity> tasks = user.getTaskSubscriptions();
        return tasks;
    }

    public void unsubscribe(Integer id) {
        TaskSubscriptionEntity taskToUpdate = taskSubscriptionRepository.findById(id).get();
        taskToUpdate.setActive(false);
        taskSubscriptionRepository.save(taskToUpdate);
    }

    public void generateTaskInstances(UserEntity user) {
        List<TaskSubscriptionEntity> subscriptions = user.getTaskSubscriptions();

        // only generate new task for a subscription if either the instances list is empty OR the last instance is in the past
        subscriptions.stream().filter(n ->
                n.getTaskInstances().isEmpty()
                        || n.getTaskInstances()
                        .get(n.getTaskInstances().size() - 1)
                        .getDueAt().isBefore(LocalDate.now().plusDays(1)))
                .forEach(this::generateNewInstanceForPeriod);

        userRepository.save(user);
    }

    public TaskInstanceEntity updateTaskInstanceCompletions(Integer id, Integer value) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(value);
        taskInstanceRepository.save(taskToUpdate);
        return taskInstanceRepository.findById(id).get();
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

}