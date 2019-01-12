package tracker.task;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.task.mapper.TaskMapper;
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
    TaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping(path = "/")
    public String index(Model model) {
        return "redirect:/web/showTaskInstances";
    }

    @GetMapping("/showTasks")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.returnTaskForUser(userService.getUser()));
        model.addAttribute("oneTimeTasks", taskService.returnOneTimeTaskForUser(userService.getUser()));
        return "showTaskSubscriptions";
    }

    @GetMapping(value = "/newTask")
    public String newTask(Model model) {
        model.addAttribute("periods", Arrays.asList(TaskPeriod.values()));
        return "newTask";
    }

    @GetMapping(value = "/newOneTimeTask")
    public String newOneTimeTask() {
        return "newOneTimeTask";
    }

    @PostMapping(value = "/newTaskSubscription")
    public String addTaskSubscription(@ModelAttribute TaskSubscriptionDTO subscription) {
        taskService.newTask(subscription);
        taskService.generateTaskInstances(userService.getUser());

        return "redirect:/web/showTasks";
    }

    @PostMapping(value = "/newOneTimeTask")
    public String addOneTimeTask(@ModelAttribute OneTimeTaskInstanceEntity oneTimeTask) {
        taskService.newOneTimeTask(oneTimeTask);

        return "redirect:/web/showTaskInstances";
    }

    @GetMapping(value = "/showTaskInstances")
    public String showUserTaskInstances(Model model) {
        // generate tasks instances prior to loading task subscriptions

        taskService.generateTaskInstances(userService.getUser());

        List<TaskSubscriptionEntity> tasks = taskService.returnTaskForUser(userService.getUser());
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());

        List<OneTimeTaskInstanceEntity> oneTimeTasks = taskService.returnOneTimeTaskForUser(userService.getUser());

        model.addAttribute("tasks", taskDTOs);
        model.addAttribute("oneTimeTasks", oneTimeTasks);
        return "showTaskInstances";
    }

    @PostMapping(value = "/tasks/{subscriptionId}/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer subscriptionId,
                                                        @PathVariable Integer id,
                                                        @PathVariable Integer value) {
        UserEntity user = userService.getUser();
        // find the relevant task subscription (will only be 1 match at most)
        TaskSubscriptionEntity task = user.getTaskSubscriptions()
                .stream()
                .filter(n -> n.getId().equals(subscriptionId))
                .collect(Collectors.toList()).get(0);
        // only update task instance if it belongs to current user
        task.getTaskInstances().stream()
                .filter(m -> m.getId().equals(id))
                .forEach(m -> taskService.updateTaskInstanceCompletions(m.getId(), value));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        UserEntity user = userService.getUser();
        // only unsubscribe from a task subscription if the id belongs to the current user
        user.getTaskSubscriptions()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> taskService.unsubscribe(m.getId()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        UserEntity user = userService.getUser();
        // only update one time task if the id belongs to the current user
        user.getOneTimeTaskInstances()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> taskService.updateOneTimeTaskCompletions(m.getId(), value));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        UserEntity user = userService.getUser();
        // only unsubscribe from a one time task if the id belongs to the current user
        user.getOneTimeTaskInstances()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> taskService.unsubscribeOneTime(m.getId()));
        return ResponseEntity.accepted().build();
    }

}
