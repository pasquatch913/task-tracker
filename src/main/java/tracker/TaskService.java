package tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskSubscriptionRepository taskSubscriptionRepository;

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    public void newTask(TaskSubscription task) {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        TaskInstance newTaskInstance = new TaskInstance(LocalDate.now().plusDays(task.getPeriod()));
        taskInstanceRepository.save(newTaskInstance);
        task.getTaskInstances().add(newTaskInstance);
        taskSubscriptionRepository.save(task);
        user.getTaskSubscriptions().add(task);
        userRepository.save(user);
    }

    public void generateTaskInstances () {
        UserEntity user = userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException());
        List<TaskSubscription> subscriptions = user.getTaskSubscriptions();
//        Stream<> stuff = subscriptions.stream().filter(n -> n.getTaskInstances()
//                .stream().filter(m -> !m.getDueAt().equals(LocalDate.now().plusDays(n.getPeriod())))).map();
        // need to move logic to compare date vs today outside of reduce
        subscriptions.stream().filter(n -> !n.getTaskInstances()
                        .stream().reduce((a,b) -> a.getDueAt().isAfter(b.getDueAt()) && a.getDueAt().isBefore(LocalDate.now().plusDays(n.getPeriod())) ? a:b).get()
                .getDueAt().equals(LocalDate.now().plusDays(n.getPeriod())) )
                .forEach(o -> {
                    TaskInstance instance = new TaskInstance(LocalDate.now().plusDays(o.getPeriod()));
                    taskInstanceRepository.save(instance);
                    o.getTaskInstances().add(instance);
                    taskSubscriptionRepository.save(o);
        });

        userRepository.save(user);

    }


//    public List<TaskSubscription> returnTaskForUser(Integer idUser){
//        userTaskService.buildUserTasks(idUser);
//        List<TaskSubscription> tasks = userRepository.findByIdUser(idUser).orElseThrow(() -> new EntityNotFoundException());
//
////        List<List<String>> tasks = userTaskRepository.findTasksByUser(idUser);
//        return tasks;
//    }

}
