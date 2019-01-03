package tracker.web;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.entity.*;
import tracker.mapper.TaskMapper;
import tracker.service.TaskService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    TaskService taskService;

    @GetMapping(path = "/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/showTasks")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.returnTaskForUser(1));
        model.addAttribute("oneTimeTasks", taskService.returnOneTimeTaskForUser(1));
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
        taskService.generateTaskInstances();

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
        taskService.generateTaskInstances();

        List<TaskSubscriptionEntity> tasks = taskService.returnTaskForUser(1);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());

        List<OneTimeTaskInstanceEntity> oneTimeTasks = taskService.returnOneTimeTaskForUser(1);

        model.addAttribute("tasks", taskDTOs);
        model.addAttribute("oneTimeTasks", oneTimeTasks);
        return "showTaskInstances";
    }

    @PostMapping(value = "/tasks/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        TaskInstanceEntity taskInstanceEntity = taskService.updateTaskInstanceCompletions(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        taskService.unsubscribe(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        OneTimeTaskInstanceEntity taskInstanceEntity = taskService.updateOneTimeTaskCompletions(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        taskService.unsubscribeOneTime(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }

}
