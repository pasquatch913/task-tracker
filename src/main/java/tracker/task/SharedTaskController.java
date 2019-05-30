package tracker.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskService;
import tracker.user.UserService;

import java.util.List;

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

}
