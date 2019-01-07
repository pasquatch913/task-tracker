package tracker.web;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tracker.task.*;
import tracker.task.mapper.TaskMapper;
import tracker.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class BffController {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskSubscriptionEntity>> getTaskSubscriptions() {
        return ResponseEntity.ok()
                .body(taskService.returnTaskForUser(userService.getUser()));
    }

    @GetMapping("/taskInstances")
    public ResponseEntity<List<TaskDTO>> getTaskInstances() {
        List<TaskDTO> tasks = taskService.returnTaskForUser(userService.getUser())
                .stream().map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/oneTimeTasks")
    public ResponseEntity<List<OneTimeTaskInstanceEntity>> getOneTimeTaskInstances() {
        return ResponseEntity.ok()
                .body(taskService.returnOneTimeTaskForUser(userService.getUser()));
    }

    @PostMapping("/tasks")
    public ResponseEntity createTaskSubscription(@RequestBody TaskSubscriptionDTO taskSubscriptionDTO) {
        taskService.newTask(taskSubscriptionDTO);
        taskService.generateTaskInstances(userService.getUser());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/tasks/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        taskService.updateTaskInstanceCompletions(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oneTimeTask")
    public ResponseEntity createOneTimeTask() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        taskService.unsubscribe(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oneTimeTasks/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        taskService.unsubscribeOneTime(id);
        return ResponseEntity.ok().build();
    }

}
