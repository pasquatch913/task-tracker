package tracker.task.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.analytics.TaskCompletionEntity;

import java.time.LocalDateTime;

@Service
public class SubscriptionCompletionService {

    @Autowired
    TaskInstanceRepository taskInstanceRepository;

    public void newTaskInstanceCompletion(Integer id, LocalDateTime time) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(taskToUpdate.getCompletions() + 1);
        taskToUpdate.getTaskCompletions().add(new TaskCompletionEntity(time));

        taskInstanceRepository.save(taskToUpdate);
    }

    public void newTaskInstanceCompletion(Integer id) {
        newTaskInstanceCompletion(id, LocalDateTime.now());
    }

    public void removeTaskCompletion(Integer id) {
        TaskInstanceEntity taskToUpdate = taskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(taskToUpdate.getCompletions() - 1);
        TaskCompletionEntity lastElement = taskToUpdate.getTaskCompletions()
                .get(taskToUpdate.getTaskCompletions().size() - 1);
        taskToUpdate.getTaskCompletions().remove(lastElement);

        taskInstanceRepository.save(taskToUpdate);
    }

}
