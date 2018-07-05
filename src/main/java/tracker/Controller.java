package tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    @Autowired
    TaskService taskService;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/showTasks")
    public ModelAndView showTasks() {
        ModelAndView mav = new ModelAndView("showTasksPage");
        //mav.addObject("tasks", taskService.returnTaskForUser(1));
        return mav;
    }

    @RequestMapping(value = "/newTask", method = RequestMethod.GET)
    public ModelAndView newTask() {
        ModelAndView mav = new ModelAndView("newTask");
        return mav;
    }

    @RequestMapping(value = "/newTaskSubscription", method = RequestMethod.POST)
    public ModelAndView addTaskSubscription(HttpServletRequest request, HttpServletResponse response,
                                            @ModelAttribute("taskSubscription") TaskSubscription subscription) {
        taskService.newTask(subscription);
        taskService.generateTaskInstances();
        return new ModelAndView("done");
    }

}
