package tracker.task.subscription;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.TaskInstanceDTO;
import tracker.task.analytics.TaskDataPointDTO;
import tracker.task.mapper.AnalyticsMapper;
import tracker.task.mapper.TaskMapper;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;
import tracker.web.EntityNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

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

    AnalyticsMapper analyticsMapper = new AnalyticsMapper();

    public void newTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(task);
        UserEntity user = userService.getUser();
        user.getTaskSubscriptions().add(taskSubscriptionEntity);
        userRepository.save(user);
    }

    public List<TaskDataPointDTO> datapointsForUser(UserEntity user) {
        return analyticsMapper.taskDataPointEntityToTaskDataPointDTO(
                taskSubscriptionRepository.findPointsByTimeAndDate(user.getId()));
    }

    public TaskSubscriptionDTO updateTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity userTaskSubscription = taskSubscriptionRepository.findById(task.getId())
                .orElseThrow(EntityNotFoundException::new);
        if (task.getName() != null && !task.getName().isEmpty()) {
            userTaskSubscription.setName(task.getName());
        }
        if (task.getNecessaryCompletions() != null) {
            userTaskSubscription.setNecessaryCompletions(task.getNecessaryCompletions());
        }
        if (task.getWeight() != null) {
            userTaskSubscription.setWeight(task.getWeight());
        }
        if (task.getPeriod() != null) {
            userTaskSubscription.setPeriod(task.getPeriod());
            List<TaskInstanceEntity> listOfTasks = userTaskSubscription.getTaskInstances();
            TaskInstanceEntity currentInstance = listOfTasks.get(listOfTasks.size() - 1);
            updateTaskInstanceDueDate(currentInstance, task.getPeriod());
        }
        if (task.getActive() == FALSE) {
            userTaskSubscription.setActive(FALSE);
        }
        taskSubscriptionRepository.save(userTaskSubscription);
        return mapper.taskSubscriptionEntityToTaskSubscriptionDTO(userTaskSubscription);
    }

    public List<TaskSubscriptionDTO> returnTaskSubscriptionsForUser(UserEntity user) {
        List<TaskSubscriptionEntity> tasks = user.getTaskSubscriptions();
        return tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskSubscriptionDTO)
                .collect(Collectors.toList());
    }

    public TaskSubscriptionDTO getTaskSubscriptionById(Integer id) {
        return taskSubscriptionRepository.findById(id)
                .map(mapper::taskSubscriptionEntityToTaskSubscriptionDTO)
                .orElseThrow(EntityNotFoundException::new);
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

    public Boolean verifyTaskInstance(UserEntity user, Integer instanceId) {
        // only search active tasks to determine if instance being updated belongs to user
        return getActiveInstances(user).stream()
                .filter(n -> n.getId().equals(instanceId))
                .collect(Collectors.toList()).size() == 1;
    }

    public List<TaskInstanceEntity> getActiveInstances(UserEntity user) {
        return getActiveSubscriptions(user).stream()
                .flatMap(n -> n.getTaskInstances()
                        .stream()
                        // TODO: should clean up as this is hacky
                        .filter(m -> m.getDueAt().isAfter(LocalDate.now().minusMonths(2))))
                .collect(Collectors.toList());
    }

    private List<TaskSubscriptionEntity> getActiveSubscriptions(UserEntity user) {
        return user.getTaskSubscriptions()
                .stream()
                .filter(TaskSubscriptionEntity::getActive)
                .collect(Collectors.toList());
    }

    private TaskInstanceEntity updateTaskInstanceDueDate(TaskInstanceEntity currentInstance, TaskPeriod newPeriod) {
        currentInstance.setDueAt(dueDateForNextInstance(newPeriod));
        taskInstanceRepository.save(currentInstance);
        return currentInstance;
    }

    private void generateNewInstanceForPeriod(TaskSubscriptionEntity taskSubscriptionEntity) {
        LocalDate nextDueDate = dueDateForNextInstance(taskSubscriptionEntity.getPeriod());
        TaskInstanceEntity newTaskInstance = new TaskInstanceEntity(nextDueDate);

        taskInstanceRepository.save(newTaskInstance);
        taskSubscriptionEntity.getTaskInstances().add(newTaskInstance);
        taskSubscriptionRepository.save(taskSubscriptionEntity);
    }

    private LocalDate dueDateForNextInstance(TaskPeriod period) {
        switch (period) {
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
                .stream()
                .filter(n ->
                        n.getDueAt().isAfter(LocalDate.now()) ||
                                n.getDueAt().isEqual(LocalDate.now())
                )
                .collect(Collectors.toList())
                .size() == 0;
    }

}
