package tracker.task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskService;
import tracker.user.UserEntity;
import tracker.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web")
public class SharedTaskController {

    private final SubscribedTaskService subscribedTaskService;

    private final OneTimeTaskService oneTimeTaskService;

    private final UserService userService;


    @GetMapping(path = "/")
    public String index(Model model) {
        return "redirect:/web/showTaskInstances";
    }

    @GetMapping("/showTaskSubscriptions")
    public String showTasks(Model model) {
        model.addAttribute("tasks", subscribedTaskService.returnTaskSubscriptionsForUser(userService.getUser()));
        model.addAttribute("oneTimeTasks", oneTimeTaskService.returnOneTimeTaskForUser(userService.getUser()));
        return "showTaskSubscriptionsView";
    }

    @GetMapping(value = "/showTaskInstances")
    public String showUserTaskInstances(Model model) {
        // generate tasks instances prior to loading task subscriptions

        subscribedTaskService.generateTaskInstances(userService.getUser());

        List<TaskInstanceDTO> taskInstances = subscribedTaskService.returnTaskInstancesForUser(userService.getUser());

        List<OneTimeTaskDTO> oneTimeTasks = oneTimeTaskService.returnOneTimeTaskForUser(userService.getUser());

        model.addAttribute("tasks", taskInstances);
        model.addAttribute("oneTimeTasks", oneTimeTasks);
        return "showTaskInstancesView";
    }

    @PostMapping(value = "/tasks/{id}/completions/{value}")
    public ResponseEntity updateTaskCompletions(@PathVariable Integer id,
                                                @PathVariable Integer value) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getTaskInstanceId().equals(id))
                    .collect(Collectors.toList()).get(0);

            subscribedTaskService.updateTaskInstanceCompletions(instance.getTaskInstanceId(), value);
            return ResponseEntity.accepted().build();
        } else if (oneTimeTaskService.verifyOneTimeTask(user, id)) {
            user.getOneTimeTaskInstances()
                    .stream()
                    .filter(n -> n.getId().equals(id))
                    .forEach(m -> oneTimeTaskService.updateOneTimeTaskCompletions(m.getId(), value));
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().body("no such task for this user");
        }
    }

    @PostMapping(value = "/tasks/complete/{id}")
    public ResponseEntity completeTask(@PathVariable Integer id) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getTaskInstanceId().equals(id))
                    .collect(Collectors.toList()).get(0);

            subscribedTaskService.unsubscribe(instance.getId());
            return ResponseEntity.accepted().build();
        } else if (oneTimeTaskService.verifyOneTimeTask(user, id)) {
            user.getOneTimeTaskInstances()
                    .stream()
                    .filter(n -> n.getId().equals(id))
                    .forEach(m -> oneTimeTaskService.unsubscribeOneTime(m.getId()));
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().body("no such task for this user");
        }
    }

}
