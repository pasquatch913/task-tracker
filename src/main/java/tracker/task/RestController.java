package tracker.task;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tracker.task.mapper.TaskMapper;
import tracker.task.onetime.OneTimeTaskController;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskController;
import tracker.task.subscription.SubscribedTaskService;
import tracker.task.subscription.TaskSubscriptionDTO;
import tracker.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class RestController {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    SubscribedTaskService subscribedTaskService;

    @Autowired
    OneTimeTaskService oneTimeTaskService;

    @Autowired
    UserService userService;

    @Autowired
    SharedTaskController sharedTaskController;

    @Autowired
    SubscribedTaskController subscribedTaskController;

    @Autowired
    OneTimeTaskController oneTimeTaskController;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getTasks() {
        // TODO: should update other controller to include method to return TaskDTO directly
        List<TaskDTO> subs = subscribedTaskService.returnTaskInstancesForUser(userService.getUser())
                .stream().map(mapper::taskInstanceDTOToTaskDTO).collect(Collectors.toList());
        List<TaskDTO> oneTimes = oneTimeTaskService.returnOneTimeTaskForUser(userService.getUser())
                .stream().map(mapper::oneTimeTaskDTOToTaskDTO).collect(Collectors.toList());
        List<TaskDTO> allTasks = new ArrayList<>();
        allTasks.addAll(subs);
        allTasks.addAll(oneTimes);
        return ResponseEntity.ok()
                .body(allTasks);
    }

    @GetMapping("/tasks/active")
    public ResponseEntity<List<TaskDTO>> getActiveTasks() {
        // TODO: should update other controller to include method to return TaskDTO directly
        List<TaskDTO> subs = subscribedTaskService.returnTaskInstancesForUser(userService.getUser())
                .stream().map(mapper::taskInstanceDTOToTaskDTO)
                .filter(TaskDTO::getActive)
                .collect(Collectors.toList());
        List<TaskDTO> oneTimes = oneTimeTaskService.returnOneTimeTaskForUser(userService.getUser())
                .stream().map(mapper::oneTimeTaskDTOToTaskDTO)
                .filter(TaskDTO::getActive)
                .collect(Collectors.toList());
        List<TaskDTO> allTasks = new ArrayList<>();
        allTasks.addAll(subs);
        allTasks.addAll(oneTimes);
        return ResponseEntity.ok()
                .body(allTasks);
    }

    @GetMapping("/taskSubscriptions")
    public ResponseEntity<List<TaskSubscriptionDTO>> getTaskSubscriptions() {
        return ResponseEntity.ok()
                .body(subscribedTaskService.returnTaskSubscriptionsForUser(userService.getUser()));
    }

    @GetMapping("/taskInstances")
    public ResponseEntity<List<TaskInstanceDTO>> getTaskInstances() {
        List<TaskInstanceDTO> tasks = subscribedTaskService.returnTaskInstancesForUser(userService.getUser());
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/oneTimeTasks")
    public ResponseEntity<List<OneTimeTaskDTO>> getOneTimeTaskInstances() {
        return ResponseEntity.ok()
                .body(oneTimeTaskService.returnOneTimeTaskForUser(userService.getUser()));
    }

    @PostMapping("tasks")
    public ResponseEntity createTaskSubscription(@RequestBody TaskSubscriptionDTO taskSubscriptionDTO) {
        subscribedTaskService.newTask(taskSubscriptionDTO);
        subscribedTaskService.generateTaskInstances(userService.getUser());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("tasks/{id}/completions/{value}")
    public ResponseEntity updateTaskCompletions(@PathVariable Integer id,
                                                @PathVariable Integer value) {
        return sharedTaskController.updateTaskCompletions(id, value);
    }

    @PostMapping("tasks/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer id,
                                                        @PathVariable Integer value) {
        return subscribedTaskController.updateTaskInstanceCompletions(id, value);
    }

    @PostMapping("/oneTimeTask")
    public ResponseEntity createOneTimeTask(@RequestBody OneTimeTaskDTO oneTimeTask) {
        oneTimeTaskService.newOneTimeTask(oneTimeTask);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/tasks/oneTime/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        return oneTimeTaskController.updateOneTimeTaskCompletions(id, value);
    }

    @PostMapping("/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        return subscribedTaskController.completeTaskSubscription(id);
    }

    @PostMapping("/oneTimeTasks/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        return oneTimeTaskController.completeOneTimeTask(id);
    }

}

