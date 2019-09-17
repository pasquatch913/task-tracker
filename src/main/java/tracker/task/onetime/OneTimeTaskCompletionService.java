package tracker.task.onetime;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.analytics.TaskCompletionEntity;
import tracker.task.mapper.TaskMapper;

import java.time.LocalDateTime;

@Service
public class OneTimeTaskCompletionService {

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository;

    private TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newTaskCompletion(Integer id, LocalDateTime time) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(taskToUpdate.getCompletions() + 1);
        taskToUpdate.getTaskCompletions().add(new TaskCompletionEntity(time));

        oneTimeTaskInstanceRepository.save(taskToUpdate);
    }

    public void newTaskCompletion(Integer id) {
        newTaskCompletion(id, LocalDateTime.now());
    }

    public void removeTaskCompletion(Integer id) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        taskToUpdate.setCompletions(taskToUpdate.getCompletions() - 1);
        TaskCompletionEntity lastElement = taskToUpdate.getTaskCompletions()
                .get(taskToUpdate.getTaskCompletions().size() - 1);
        taskToUpdate.getTaskCompletions().remove(lastElement);

        oneTimeTaskInstanceRepository.save(taskToUpdate);
    }
}
