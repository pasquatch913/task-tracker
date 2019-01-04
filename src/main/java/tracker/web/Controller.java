package tracker.web;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.entity.*;
import tracker.mapper.TaskMapper;
import tracker.service.TaskService;
import tracker.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping(path = "/")
    public String index(Model model) {
        return "index";
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

        return "redirect:/showTasks";
    }

    @PostMapping(value = "/newOneTimeTask")
    public String addTaskSubscription(@ModelAttribute OneTimeTaskInstanceEntity oneTimeTask) {
        taskService.newOneTimeTask(oneTimeTask);

        return "redirect:/showTaskInstances";
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
        return ResponseEntity.ok().build();
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
        return ResponseEntity.ok().build();
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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "userRegistration";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserDTO userDTO) {
        Boolean success = userService.createUser(userDTO);
        if (!success) return "redirect:/register?duplicateUsername";
        else return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

}
