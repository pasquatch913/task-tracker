package tracker.task.onetime;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tracker.user.UserEntity;
import tracker.user.UserService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/oneTimes")
public class OneTimeTaskController {

    private final OneTimeTaskService oneTimeTaskService;

    private final UserService userService;

    @GetMapping(value = "/newTask")
    public String newOneTimeTask() {
        return "newOneTimeTaskView";
    }

    @PostMapping(value = "/newTask")
    public String addOneTimeTask(@ModelAttribute("newOneTimeTask") OneTimeTaskDTO oneTimeTask) {
        oneTimeTaskService.newOneTimeTask(oneTimeTask);

        return "redirect:/web/showTaskInstances";
    }

    @GetMapping(value = "/updateTask/{taskId}")
    public String updateTaskView(Model model, @PathVariable Integer taskId) {
        model.addAttribute("task", oneTimeTaskService.getOneTimeTaskById(taskId));
        return "updateOneTimeTaskView";
    }

    @PostMapping(value = "/updateTask")
    public String updateTask(@ModelAttribute("updateOneTimeTaskRequest") OneTimeTaskDTO updateTaskRequest) {
        oneTimeTaskService.updateOneTimeTask(updateTaskRequest);
        return "redirect:/web/showTaskSubscriptions";
    }

    @PostMapping(value = "/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        UserEntity user = userService.getUser();
        // only update one time task if the id belongs to the current user
        user.getOneTimeTaskInstances()
                .stream()
                .filter(n -> n.getId().equals(id))
                .forEach(m -> oneTimeTaskService.updateOneTimeTaskCompletions(m.getId(), value));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/complete/{id}")
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
