package tracker.task.onetime;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.task.analytics.TaskCompletionEntity;
import tracker.task.mapper.TaskMapper;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;
import tracker.web.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

@Service
public class OneTimeTaskService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository;

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    public void newOneTimeTask(OneTimeTaskDTO oneTimeTask) {
        UserEntity user = userService.getUser();
        user.getOneTimeTaskInstances().add(mapper.oneTimeTaskDTOToOneTimeTaskInstanceEntity(oneTimeTask));
        userRepository.save(user);
    }

    // TODO not sure that this is needed.
    // now that we're eagerly loading the user tasks, this is accessing the repo unnecessarily
    public List<OneTimeTaskDTO> returnOneTimeTaskForUser(UserEntity user) {
        List<OneTimeTaskDTO> tasks = user.getOneTimeTaskInstances().stream()
                .map(mapper::oneTimeTaskInstanceEntityToOneTimeTaskDTO)
                .collect(Collectors.toList());
        return tasks;
    }

    public OneTimeTaskDTO getOneTimeTaskById(Integer taskId) {
        return oneTimeTaskInstanceRepository.findById(taskId)
                .map(mapper::oneTimeTaskInstanceEntityToOneTimeTaskDTO)
                .orElseThrow(EntityNotFoundException::new);
    }

    public OneTimeTaskDTO updateOneTimeTask(OneTimeTaskDTO task) {
        OneTimeTaskInstanceEntity userTask = oneTimeTaskInstanceRepository.findById(task.getId())
                .orElseThrow(EntityNotFoundException::new);
        if (task.getName() != null && !task.getName().isEmpty()) {
            userTask.setName(task.getName());
        }
        if (task.getNecessaryCompletions() != null) {
            userTask.setNecessaryCompletions(task.getNecessaryCompletions());
        }
        if (task.getWeight() != null) {
            userTask.setWeight(task.getWeight());
        }
        if (task.getDueDate() != null) {
            userTask.setDueDate(task.getDueDate());
        }
        if (task.getActive() == FALSE) {
            userTask.setActive(FALSE);
        }
        oneTimeTaskInstanceRepository.save(userTask);
        return mapper.oneTimeTaskInstanceEntityToOneTimeTaskDTO(userTask);
    }

    public Boolean verifyOneTimeTask(UserEntity user, Integer taskId) {
        return user.getOneTimeTaskInstances().stream()
                .filter(n -> n.getId().equals(taskId))
                .collect(Collectors.toList()).size() == 1;
    }

    public OneTimeTaskDTO updateOneTimeTaskCompletions(Integer id, Integer value) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        if (value > taskToUpdate.getCompletions()) {
            taskToUpdate.setCompletions(value);
            while (taskToUpdate.getTaskCompletions().size() < value) {
                taskToUpdate.getTaskCompletions().add(new TaskCompletionEntity());
            }
        }
        if (0 < value && value < taskToUpdate.getCompletions()) {
            taskToUpdate.setCompletions(value);
            while (taskToUpdate.getTaskCompletions().size() > value) {
                TaskCompletionEntity lastElement = taskToUpdate.getTaskCompletions()
                        .get(taskToUpdate.getTaskCompletions().size() - 1);
                taskToUpdate.getTaskCompletions().remove(lastElement);
            }
        }
        oneTimeTaskInstanceRepository.save(taskToUpdate);
        return mapper.oneTimeTaskInstanceEntityToOneTimeTaskDTO(oneTimeTaskInstanceRepository.findById(id).get());
    }

    public void unsubscribeOneTime(Integer id) {
        OneTimeTaskInstanceEntity taskToUpdate = oneTimeTaskInstanceRepository.findById(id).get();
        taskToUpdate.setActive(false);
        oneTimeTaskInstanceRepository.save(taskToUpdate);
    }

}
