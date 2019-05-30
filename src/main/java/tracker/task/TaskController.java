package tracker.task;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.task.mapper.TaskMapper;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskService;
import tracker.task.subscription.TaskPeriod;
import tracker.task.subscription.TaskSubscriptionDTO;
import tracker.user.UserEntity;
import tracker.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class TaskController {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    SubscribedTaskService subscribedTaskService;

    @Autowired
    OneTimeTaskService oneTimeTaskService;

    @Autowired
    UserService userService;

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

    @GetMapping(value = "/newTaskSubscription")
    public String newTask(Model model) {
        model.addAttribute("periods", Arrays.asList(TaskPeriod.values()));
        return "newTaskSubscriptionView";
    }

    @GetMapping(value = "/newOneTimeTask")
    public String newOneTimeTask() {
        return "newOneTimeTaskView";
    }

    @PostMapping(value = "/newTaskSubscription")
    public String addTaskSubscription(@ModelAttribute("newTaskSubscription") TaskSubscriptionDTO subscription) {
        subscribedTaskService.newTask(subscription);
        subscribedTaskService.generateTaskInstances(userService.getUser());

        return "redirect:/web/showTasks";
    }

    @PostMapping(value = "/newOneTimeTask")
    public String addOneTimeTask(@ModelAttribute("newOneTimeTask") OneTimeTaskDTO oneTimeTask) {
        oneTimeTaskService.newOneTimeTask(oneTimeTask);

        return "redirect:/web/showTaskInstances";
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

    @PostMapping(value = "/tasks/{subscriptionId}/instances/{id}/completions/{value}")
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

    @PostMapping(value = "/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        UserEntity user = userService.getUser();
        // only unsubscribe from a task subscription if the id belongs to the current user
        user.getTaskSubscriptions()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> subscribedTaskService.unsubscribe(m.getId()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        UserEntity user = userService.getUser();
        // only update one time task if the id belongs to the current user
        user.getOneTimeTaskInstances()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> oneTimeTaskService.updateOneTimeTaskCompletions(m.getId(), value));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        UserEntity user = userService.getUser();
        // only unsubscribe from a one time task if the id belongs to the current user
        user.getOneTimeTaskInstances()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> oneTimeTaskService.unsubscribeOneTime(m.getId()));
        return ResponseEntity.accepted().build();
    }

}
