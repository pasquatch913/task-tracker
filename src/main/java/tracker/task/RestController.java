package tracker.task;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tracker.task.mapper.TaskMapper;
import tracker.task.onetime.OneTimeTaskDTO;
import tracker.task.onetime.OneTimeTaskInstanceEntity;
import tracker.task.onetime.OneTimeTaskService;
import tracker.task.subscription.SubscribedTaskService;
import tracker.task.subscription.TaskSubscriptionDTO;
import tracker.user.UserService;

import java.util.List;

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
    TaskController taskController;

    @GetMapping("/tasks")
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

    @PostMapping("tasks/{subscriptionId}/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer subscriptionId,
                                                        @PathVariable Integer id,
                                                        @PathVariable Integer value) {
        return taskController.updateTaskInstanceCompletions(subscriptionId, id, value);
    }

    @PostMapping("/tasks/oneTime/{id}/completions/value/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        return taskController.updateOneTimeTaskCompletions(id, value);
    }

    @PostMapping("/oneTimeTask")
    public ResponseEntity createOneTimeTask(@RequestBody OneTimeTaskInstanceEntity oneTimeTask) {
        oneTimeTaskService.newOneTimeTask(oneTimeTask);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        return taskController.completeTaskSubscription(id);
    }

    @PostMapping("/oneTimeTasks/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        return taskController.completeOneTimeTask(id);
    }

}

