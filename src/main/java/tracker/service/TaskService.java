package tracker.service;

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

    public void newTask(TaskSubscriptionEntity task) {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        TaskSubscriptionEntity updatedTaskSubscription = generateFirstInstance(task);
        taskSubscriptionRepository.save(updatedTaskSubscription);
        user.getTaskSubscriptions().add(updatedTaskSubscription);
        userRepository.save(user);
    }

    private TaskSubscriptionEntity generateFirstInstance (TaskSubscriptionEntity taskSubscription) {
        TaskInstanceEntity newTaskInstance = new TaskInstanceEntity(LocalDate.now().plusDays(taskSubscription.getPeriod()));
        taskInstanceRepository.save(newTaskInstance);
        taskSubscription.getTaskInstances().add(newTaskInstance);
        return taskSubscription;
    }

    public void generateTaskInstances () {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        List<TaskSubscriptionEntity> subscriptions = user.getTaskSubscriptions();
        subscriptions.stream().filter(n -> !n.getTaskInstances()
                        .stream().reduce((a,b) -> a.getDueAt().isAfter(b.getDueAt()) && a.getDueAt().isBefore(LocalDate.now().plusDays(n.getPeriod())) ? a:b).get()
                .getDueAt().equals(LocalDate.now().plusDays(n.getPeriod())) )
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

}
