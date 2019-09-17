package tracker.task.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.user.UserService;

import java.util.Arrays;

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

    @GetMapping(value = "/updateTask/{subscriptionId}")
    public String updateTaskView(Model model, @PathVariable Integer subscriptionId) {
        model.addAttribute("periods", Arrays.asList(TaskPeriod.values()));
        model.addAttribute("subscription", subscribedTaskService.getTaskSubscriptionById(subscriptionId));
        return "updateTaskSubscriptionView";
    }

    @PostMapping(value = "/updateTask")
    public String updateTask(@ModelAttribute("updateTaskSubscriptionRequest") TaskSubscriptionDTO updateTaskSubscriptionRequest) {
        subscribedTaskService.updateTask(updateTaskSubscriptionRequest);
        return "redirect:/web/showTaskSubscriptions";
    }

    @PostMapping(value = "/newTask")
    public String addTaskSubscription(@ModelAttribute("newTaskSubscription") TaskSubscriptionDTO subscription) {
        subscribedTaskService.newTask(subscription);
        subscribedTaskService.generateTaskInstances(userService.getUser());

        return "redirect:/web/showTaskSubscriptions";
    }

}
