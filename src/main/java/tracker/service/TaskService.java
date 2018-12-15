package tracker.service;

import mapper.TaskMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.EntityNotFoundException;
import tracker.entity.TaskInstanceEntity;
import tracker.entity.TaskSubscriptionEntity;
import tracker.entity.UserEntity;
import tracker.repository.TaskInstanceRepository;
import tracker.repository.TaskSubscriptionRepository;
import tracker.repository.UserRepository;

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

    public void newTask(TaskSubscriptionEntity task) {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        taskSubscriptionRepository.save(task);
        user.getTaskSubscriptions().add(task);
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
                .forEach(o -> {
                    TaskInstanceEntity instance = new TaskInstanceEntity(LocalDate.now().plusDays(o.getPeriod()));
                    taskInstanceRepository.save(instance);
                    o.getTaskInstances().add(instance);
                    taskSubscriptionRepository.save(o);
        });
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

}
