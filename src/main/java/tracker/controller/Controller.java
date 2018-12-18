package tracker.controller;

import mapper.TaskMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tracker.entity.*;
import tracker.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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

    @GetMapping(value = "/newTask")
    public ModelAndView newTask() {
        ModelAndView mav = new ModelAndView("newTask");
        mav.addObject("periods", Arrays.asList(TaskPeriod.values()));
        return mav;
    }

    @PostMapping(value = "/newTaskSubscription")
    public RedirectView addTaskSubscription(HttpServletRequest request, HttpServletResponse response,
                                            @ModelAttribute("newTaskSubscription") TaskSubscriptionDTO subscription) {
        taskService.newTask(subscription);
        taskService.generateTaskInstances();

        return new RedirectView("/showTasks");
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

    @DeleteMapping(value = "/tasks/{id}")
    public ResponseEntity deleteTaskSubscription(@PathVariable Integer id) {
        taskService.unsubscribe(id);
        return ResponseEntity.accepted().build();
    }
}
