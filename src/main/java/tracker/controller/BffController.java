package tracker.controller;

import mapper.TaskMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tracker.entity.OneTimeTaskInstanceEntity;
import tracker.entity.TaskDTO;
import tracker.entity.TaskSubscriptionEntity;
import tracker.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bff")
public class BffController {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskSubscriptionEntity>> getTaskSubscriptions() {
        return ResponseEntity.ok()
                .body(taskService.returnTaskForUser(1));
    }

    @GetMapping("/taskInstances")
    public ResponseEntity<List<TaskDTO>> getTaskInstances() {
        List<TaskDTO> tasks = taskService.returnTaskForUser(1)
                .stream().map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/oneTimeTasks")
    public ResponseEntity<List<OneTimeTaskInstanceEntity>> getOneTimeTaskInstances() {
        return ResponseEntity.ok()
                .body(taskService.returnOneTimeTaskForUser(1));
    }

    @PostMapping("/tasks")
    public ResponseEntity createTaskSubscription() {
        return ResponseEntity.ok().build();
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
