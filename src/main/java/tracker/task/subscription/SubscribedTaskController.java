package tracker.task.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.task.TaskInstanceDTO;
import tracker.user.UserEntity;
import tracker.user.UserService;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/subscriptions")
public class SubscribedTaskController {

    private final SubscribedTaskService subscribedTaskService;

    private final UserService userService;

    @GetMapping(value = "/newTask")
    public String newTask(Model model) {
        model.addAttribute("periods", Arrays.asList(TaskPeriod.values()));
        return "newTaskSubscriptionView";
    }

    @PostMapping(value = "/newTask")
    public String addTaskSubscription(@ModelAttribute("newTaskSubscription") TaskSubscriptionDTO subscription) {
        subscribedTaskService.newTask(subscription);
        subscribedTaskService.generateTaskInstances(userService.getUser());

        return "redirect:/web/showTaskSubscriptions";
    }

    @PostMapping(value = "/{subscriptionId}/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer subscriptionId,
                                                        @PathVariable Integer id,
                                                        @PathVariable Integer value) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, subscriptionId, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getTaskInstanceId().equals(id))
                    .collect(Collectors.toList()).get(0);

            subscribedTaskService.updateTaskInstanceCompletions(instance.getTaskInstanceId(), value);
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().body("no such task for this user");
        }
    }

    @PostMapping(value = "/complete/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        UserEntity user = userService.getUser();
        // only unsubscribe from a task subscription if the id belongs to the current user
        user.getTaskSubscriptions()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> subscribedTaskService.unsubscribe(m.getId()));
        return ResponseEntity.accepted().build();
    }

}
