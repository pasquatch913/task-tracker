package tracker.controller;

import mapper.TaskMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import tracker.entity.TaskDTO;
import tracker.entity.TaskInstanceEntity;
import tracker.entity.TaskSubscriptionEntity;
import tracker.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Autowired
    TaskService taskService;

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/showTasks")
    public ModelAndView showTasks() {
        ModelAndView mav = new ModelAndView("showTaskSubscriptions");
        mav.addObject("tasks", taskService.returnTaskForUser(1));
        return mav;
    }

    @RequestMapping(value = "/newTask", method = RequestMethod.GET)
    public ModelAndView newTask() {
        ModelAndView mav = new ModelAndView("newTask");
        return mav;
    }

    @RequestMapping(value = "/newTaskSubscription", method = RequestMethod.POST)
    public ModelAndView addTaskSubscription(HttpServletRequest request, HttpServletResponse response,
                                            @ModelAttribute("taskSubscription") TaskSubscriptionEntity subscription) {
        taskService.newTask(subscription);
        taskService.generateTaskInstances();
        return new ModelAndView("done");
    }

    @RequestMapping(value = "/showTaskInstances")
    public ModelAndView showUserTaskInstances () {
        // generate tasks instances prior to loading task subscriptions
        taskService.generateTaskInstances();

        List<TaskSubscriptionEntity> tasks = taskService.returnTaskForUser(1);
        List<TaskDTO> taskDTO = tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());
        ModelAndView mav = new ModelAndView("showTaskInstances");
        mav.addObject("tasks", taskDTO);
        return mav;
    }

    @PostMapping(value = "/tasks/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        TaskInstanceEntity taskInstanceEntity = taskService.updateTaskInstanceCompletions(id, value);
        return ResponseEntity.ok(taskInstanceEntity);
    }
}
