package tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class TaskController {

    @Autowired
    TaskRepository repository;

    TaskService taskService = new TaskService();

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/showTasks")
    public String showTasks(@RequestParam(name="name", required = false, defaultValue = "World") String name, Model model) {
        //model.addAttribute("task", taskService.returnTask());
        //return "showTasksPage";
        String value = "";
        TaskEntity task = repository.findById(7).orElseThrow(() -> new EntityNotFoundException());
        value = String.valueOf(task.getName());
        return "Here is the task name:" + value;
    }


}
