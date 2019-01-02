package tracker.service;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.EntityNotFoundException;
import tracker.entity.*;
import tracker.mapper.TaskMapper;
import tracker.repository.OneTimeTaskInstanceRepository;
import tracker.repository.TaskInstanceRepository;
import tracker.repository.TaskSubscriptionRepository;
import tracker.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskSubscriptionRepository taskSubscriptionRepository;

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository;

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(task);
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        taskSubscriptionRepository.save(taskSubscriptionEntity);
        user.getTaskSubscriptions().add(taskSubscriptionEntity);
        userRepository.save(user);
    }

    public List<TaskSubscriptionEntity> returnTaskForUser(Integer idUser){
        generateTaskInstances();
        List<TaskSubscriptionEntity> tasks = userRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException())
                .getTaskSubscriptions();
        return tasks;
    }

    public void unsubscribe(Integer id) {
        TaskSubscriptionEntity taskToUpdate = taskSubscriptionRepository.findById(id).get();
        taskToUpdate.setActive(false);
        taskSubscriptionRepository.save(taskToUpdate);
    }

    public void generateTaskInstances() {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
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
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        oneTimeTaskInstanceRepository.save(taskInstanceEntity);

        user.getOneTimeTaskInstances().add(taskInstanceEntity);
        userRepository.save(user);
    }

    public List<OneTimeTaskInstanceEntity> returnOneTimeTaskForUser(Integer idUser) {
        List<OneTimeTaskInstanceEntity> tasks = userRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException())
                .getOneTimeTaskInstances();
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
                taskInstanceEntity.setDueAt(LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1).plusWeeks(1));
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
