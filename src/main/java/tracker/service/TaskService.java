package tracker.service;

import mapper.TaskMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.EntityNotFoundException;
import tracker.entity.TaskInstanceEntity;
import tracker.entity.TaskSubscriptionDTO;
import tracker.entity.TaskSubscriptionEntity;
import tracker.entity.UserEntity;
import tracker.repository.TaskInstanceRepository;
import tracker.repository.TaskSubscriptionRepository;
import tracker.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskSubscriptionRepository taskSubscriptionRepository;

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newTask(TaskSubscriptionDTO task) {
        TaskSubscriptionEntity taskSubscriptionEntity = mapper.taskSubscriptionDTOToTaskSubscriptionEntity(task);
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        taskSubscriptionRepository.save(taskSubscriptionEntity);
        user.getTaskSubscriptions().add(taskSubscriptionEntity);
        userRepository.save(user);
    }

    public void generateTaskInstances () {
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


    public List<TaskSubscriptionEntity> returnTaskForUser(Integer idUser){
        generateTaskInstances();
        List<TaskSubscriptionEntity> tasks = userRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException())
                .getTaskSubscriptions();
        return tasks;
    }

    public TaskInstanceEntity updateTaskInstanceCompletions(Integer id, Integer value) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(value);
        taskInstanceRepository.save(taskToUpdate);
        return taskInstanceRepository.findById(id).get();
    }

    public void unsubscribe(Integer id) {
        TaskSubscriptionEntity taskToUpdate = taskSubscriptionRepository.findById(id).get();
        taskToUpdate.setActive(false);
        taskSubscriptionRepository.save(taskToUpdate);
    }

    private void generateNewInstanceForPeriod(TaskSubscriptionEntity taskSubscriptionEntity) {
        TaskInstanceEntity taskInstanceEntity = new TaskInstanceEntity();
        switch (taskSubscriptionEntity.getPeriod()) {
            case DAILY:
                taskInstanceEntity.setDueAt(LocalDate.now());
                break;
            case WEEKLY:
                taskInstanceEntity.setDueAt(LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1));
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
