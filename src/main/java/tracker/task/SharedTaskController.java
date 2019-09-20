package tracker.task;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.task.analytics.TaskDataPointDTO;
import tracker.task.mapper.TaskMapper;
import tracker.task.onetime.OneTimeTaskCompletionService;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskInstanceEntity;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskService;
import tracker.task.subscription.SubscriptionCompletionService;
import tracker.user.UserEntity;
import tracker.user.UserService;
import tracker.web.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web")
public class SharedTaskController {

    private final SubscribedTaskService subscribedTaskService;

    private final SubscriptionCompletionService subscriptionCompletionService;

    private final OneTimeTaskService oneTimeTaskService;

    private final OneTimeTaskCompletionService oneTimeTaskCompletionService;

    private final UserService userService;

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

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

        List<TaskDTO> allTasks = taskInstances.stream()
                .map(mapper::taskInstanceDTOToTaskDTO)
                .collect(Collectors.toList());
        allTasks.addAll(
                oneTimeTasks.stream()
                        .map(mapper::oneTimeTaskDTOToTaskDTO)
                        .collect(Collectors.toList()));

        model.addAttribute("tasks", allTasks);
        return "showTaskInstancesView";
    }

    @GetMapping(value = "/taskData")
    public String showUserTaskCompletionData(Model model) {
        List<TaskDataPointDTO> subscriptionData = subscribedTaskService.datapointsForUser(userService.getUser());
        model.addAttribute("subscriptionData", subscriptionData);
        return "showTaskDataView";
    }

    @PostMapping(value = "/tasks/complete/{id}")
    public ResponseEntity newTaskCompletion(@PathVariable Integer id,
                                            @RequestParam(value = "time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime time) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getId().equals(id))
                    .collect(Collectors.toList()).get(0);

            if (time == null) {
                subscriptionCompletionService.newTaskInstanceCompletion(instance.getId());
            } else {
                subscriptionCompletionService.newTaskInstanceCompletion(instance.getId(), time);
            }
            return ResponseEntity.accepted().build();
        } else if (oneTimeTaskService.verifyOneTimeTask(user, id)) {
            OneTimeTaskInstanceEntity task = user.getOneTimeTaskInstances()
                    .stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            if (time == null) {
                oneTimeTaskCompletionService.newTaskCompletion(task.getId());
            } else {
                oneTimeTaskCompletionService.newTaskCompletion(task.getId(), time);
            }

            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().body("no such task for this user");
        }
    }

    @PostMapping(value = "/tasks/uncomplete/{id}")
    public ResponseEntity removeTaskCompletion(@PathVariable Integer id) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getId().equals(id))
                    .collect(Collectors.toList()).get(0);

            subscriptionCompletionService.removeTaskCompletion(instance.getId());
            return ResponseEntity.accepted().build();
        } else if (oneTimeTaskService.verifyOneTimeTask(user, id)) {
            user.getOneTimeTaskInstances()
                    .stream()
                    .filter(n -> n.getId().equals(id))
                    .forEach(m -> oneTimeTaskCompletionService.removeTaskCompletion(m.getId()));
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.badRequest().body("no such task for this user");
        }
    }

    @PostMapping(value = "/tasks/deactivate/{id}")
    public ResponseEntity deactivateTask(@PathVariable Integer id) {
        UserEntity user = userService.getUser();

        // only update task instance if it belongs to current user
        if (subscribedTaskService.verifyTaskInstance(user, id)) {
            TaskInstanceDTO instance = subscribedTaskService.returnTaskInstancesForUser(user).stream()
                    .filter(n -> n.getId().equals(id))
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
