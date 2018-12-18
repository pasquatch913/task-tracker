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
        mav.addObject("oneTimeTasks", taskService.returnOneTimeTaskForUser(1));
        return mav;
    }

    @GetMapping(value = "/newTask")
    public ModelAndView newTask() {
        ModelAndView mav = new ModelAndView("newTask");
        mav.addObject("periods", Arrays.asList(TaskPeriod.values()));
        return mav;
    }

    @GetMapping(value = "/newOneTimeTask")
    public ModelAndView newOneTimeTask() {
        ModelAndView mav = new ModelAndView("newOneTimeTask");
        return mav;
    }

    @PostMapping(value = "/newTaskSubscription")
    public RedirectView addTaskSubscription(HttpServletRequest request, HttpServletResponse response,
                                            @ModelAttribute("newTaskSubscription") TaskSubscriptionDTO subscription) {
        taskService.newTask(subscription);
        taskService.generateTaskInstances();

        return new RedirectView("/showTasks");
    }

    @PostMapping(value = "/newOneTimeTask")
    public RedirectView addTaskSubscription(HttpServletRequest request, HttpServletResponse response,
                                            @ModelAttribute("newOneTimeTask") OneTimeTaskInstanceEntity oneTimeTask) {
        taskService.newOneTimeTask(oneTimeTask);

        return new RedirectView("/showTaskInstances");
    }

    @RequestMapping(value = "/showTaskInstances")
    public ModelAndView showUserTaskInstances () {
        // generate tasks instances prior to loading task subscriptions
        taskService.generateTaskInstances();

        List<TaskSubscriptionEntity> tasks = taskService.returnTaskForUser(1);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(mapper::taskSubscriptionEntityToTaskDTO)
                .collect(Collectors.toList());

        List<OneTimeTaskInstanceEntity> oneTimeTasks = taskService.returnOneTimeTaskForUser(1);

        ModelAndView mav = new ModelAndView("showTaskInstances");
        mav.addObject("tasks", taskDTOs);
        mav.addObject("oneTimeTasks", oneTimeTasks);
        return mav;
    }

    @PostMapping(value = "/tasks/instances/{id}/completions/{value}")
    public ResponseEntity updateTaskInstanceCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        TaskInstanceEntity taskInstanceEntity = taskService.updateTaskInstanceCompletions(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/tasks/{id}")
    public ResponseEntity completeTaskSubscription(@PathVariable Integer id) {
        taskService.unsubscribe(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}/completions/{value}")
    public ResponseEntity updateOneTimeTaskCompletions(@PathVariable Integer id, @PathVariable Integer value) {
        OneTimeTaskInstanceEntity taskInstanceEntity = taskService.updateOneTimeTaskCompletions(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/tasks/oneTime/{id}")
    public ResponseEntity completeOneTimeTask(@PathVariable Integer id) {
        taskService.unsubscribeOneTime(id);
        return ResponseEntity.accepted().build();
    }
}
