package tracker.task.onetime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tracker.user.UserEntity;
import tracker.user.UserRepository;
import tracker.user.UserService;

import java.util.List;

@Service
public class OneTimeTaskService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository;

    public void newOneTimeTask(OneTimeTaskInstanceEntity taskInstanceEntity) {
        UserEntity user = userService.getUser();
        user.getOneTimeTaskInstances().add(taskInstanceEntity);
        userRepository.save(user);
    }

    // TODO not sure that this is needed.
    // now that we're eagerly loading the user tasks, this is accessing the repo unnecessarily
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

}
