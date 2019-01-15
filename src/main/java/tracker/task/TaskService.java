package tracker.task;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.mapper.TaskMapper;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TaskSubscriptionRepository taskSubscriptionRepository;

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository;

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(task);
        UserEntity user = userService.getUser();
        taskSubscriptionRepository.save(taskSubscriptionEntity);
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
                        .getDueAt().isBefore(LocalDate.now()))
                .forEach(o -> generateNewInstanceForPeriod(o));

        userRepository.save(user);
    }

    public TaskInstanceEntity updateTaskInstanceCompletions(Integer id, Integer value) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(value);
        taskInstanceRepository.save(taskToUpdate);
        return taskInstanceRepository.findById(id).get();
    }

    public void newOneTimeTask(OneTimeTaskInstanceEntity taskInstanceEntity) {
        UserEntity user = userService.getUser();
        oneTimeTaskInstanceRepository.save(taskInstanceEntity);

        user.getOneTimeTaskInstances().add(taskInstanceEntity);
        userRepository.save(user);
    }

    public List<OneTimeTaskInstanceEntity> returnOneTimeTaskForUser(UserEntity user) {
        List<OneTimeTaskInstanceEntity> tasks = user.getOneTimeTaskInstances();
        return tasks;
    }

    public OneTimeTaskInstanceEntity updateOneTimeTaskCompletions(Integer id, Integer value) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(value);
        oneTimeTaskInstanceRepository.save(taskToUpdate);
        return oneTimeTaskInstanceRepository.findById(id).get();
    }

    public void unsubscribeOneTime(Integer id) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        taskToUpdate.setActive(false);
        oneTimeTaskInstanceRepository.save(taskToUpdate);
    }

    private void generateNewInstanceForPeriod(TaskSubscriptionEntity taskSubscriptionEntity) {
        TaskInstanceEntity taskInstanceEntity = new TaskInstanceEntity();
        switch (taskSubscriptionEntity.getPeriod()) {
            case DAILY:
                taskInstanceEntity.setDueAt(LocalDate.now());
                break;
            case WEEKLY:
                taskInstanceEntity.setDueAt(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
                break;
            case MONTHLY:
                taskInstanceEntity.setDueAt(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
                break;
            default:
                return;
        }
        taskInstanceRepository.save(taskInstanceEntity);
        taskSubscriptionEntity.getTaskInstances().add(taskInstanceEntity);
        taskSubscriptionRepository.save(taskSubscriptionEntity);
    }

}
