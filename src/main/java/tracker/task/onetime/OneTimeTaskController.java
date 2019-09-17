package tracker.task.onetime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

}
