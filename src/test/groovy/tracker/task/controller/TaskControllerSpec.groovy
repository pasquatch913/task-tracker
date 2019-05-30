package tracker.task.controller


import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.TaskController
import tracker.task.TaskInstanceDTO
import tracker.task.onetime.OneTimeTaskDTO
import tracker.task.onetime.OneTimeTaskService
import tracker.task.subscription.SubscribedTaskService
import tracker.task.subscription.TaskPeriod
import tracker.task.subscription.TaskSubscriptionDTO
import tracker.user.UserEntity
import tracker.user.UserRolesEntity
import tracker.user.UserService

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class TaskControllerSpec extends Specification {

    MockMvc mockMvc

    @Subject
    TaskController controller = new TaskController()

    UserService mockUserService = Mock()
    SubscribedTaskService mockSubService = Mock()
    OneTimeTaskService mockOneTimeService = Mock()

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        controller.subscribedTaskService = mockSubService
        controller.userService = mockUserService
        controller.oneTimeTaskService = mockOneTimeService
    }

    def taskSubs = [new TaskSubscriptionDTO(name: "my first subscription", necessaryCompletions: 2,
            weight: 3, period: TaskPeriod.DAILY),
                    new TaskSubscriptionDTO(name: "my second subscription", necessaryCompletions: 3,
                            weight: 2, period: TaskPeriod.WEEKLY),
    ]

    def taskInstances = [new TaskInstanceDTO(id: 3, name: "my first subscription", weight: 3, necessaryCompletions: 2,
            completions: 1, dueDate: LocalDate.now().minusDays(1), taskInstanceId: 11, active: true),
                         new TaskInstanceDTO(id: 3, name: "my first subscription", weight: 3, necessaryCompletions: 2,
                                 completions: 1, dueDate: LocalDate.now(), taskInstanceId: 12, active: true),
                         new TaskInstanceDTO(id: 6, name: "my second subscription", weight: 2, necessaryCompletions: 3,
                                 completions: 0, dueDate: LocalDate.now().plusDays(4), taskInstanceId: 13, active: true)]

    def taskOneTimes = [new OneTimeTaskDTO(id: 2, name: "my first one time", weight: 3,
            necessaryCompletions: 6, completions: 2, dueDate: LocalDate.now().plusDays(7)),
                        new OneTimeTaskDTO(id: 4, name: "my big one time", weight: 20,
                                necessaryCompletions: 2, completions: 0, dueDate: LocalDate.now().plusDays(60)),
                        new OneTimeTaskDTO(id: 5, name: "my last one time", weight: 5,
                                necessaryCompletions: 3, completions: 0, dueDate: LocalDate.now().plusDays(2))]

    def user = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
            taskSubscriptions: [], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])

    def "root route returns redirect"() {
        when:
        def response = mockMvc.perform(get("/web/")).andReturn().response
        def body = response.content

        then:
        println(response)
        response.status == 302
        body != null
    }

    def "requests to show tasks return view containing both one time and subscribed tasks"() {
        when:
        def response = mockMvc.perform(get("/web/showTasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("showTaskSubscriptions"))
                .andExpect(model().attributeExists("tasks", "oneTimeTasks"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        2 * mockUserService.getUser() >> user
        1 * mockSubService.returnTaskSubscriptionsForUser(_) >> taskSubs
        1 * mockOneTimeService.returnOneTimeTaskForUser(_) >> taskOneTimes
        model.size() == 2
        model.get("tasks").get(0).name == taskSubs.get(0).name
        model.get("oneTimeTasks").get(1).name == taskOneTimes.get(1).name
    }

    def "requests for the create new subscriptions page return the newTask view"() {
        when:
        def response = mockMvc.perform(get("/web/newTaskSubscription"))
                .andExpect(status().isOk())
                .andExpect(view().name("newTaskSubscriptionView"))
                .andExpect(model().attributeExists("periods"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        model.get("periods").size() == Arrays.asList(TaskPeriod.values()).size()
    }

    def "requests for the create one time task page return appropriate view"() {
        when:
        def response = mockMvc.perform(get("/web/newOneTimeTask"))
                .andExpect(status().isOk())
                .andExpect(view().name("newOneTimeTaskView"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        model.size() == 0
    }

    def "requests to create new task subscription invoke service methods and redirect"() {
        given:
        def request = new TaskSubscriptionDTO(name: "my new subscription",
                necessaryCompletions: 1, weight: 1, period: TaskPeriod.MONTHLY)

        when:
        def response = mockMvc.perform(post("/web/newTaskSubscription")
                .flashAttr("newTaskSubscription", request))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/web/showTasks"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockSubService.newTask(request)
        1 * mockUserService.getUser() >> user
        1 * mockSubService.generateTaskInstances(_)
        model.get("newTaskSubscription").name == request.name
    }

    def "requests to create new one time task invoke service methods and redirect"() {
        given:
        def request = new OneTimeTaskDTO(id: 999, name: "my new one time task",
                necessaryCompletions: 1, weight: 1, dueDate: LocalDate.MAX)

        when:
        def response = mockMvc.perform(post("/web/newOneTimeTask")
                .flashAttr("newOneTimeTask", request))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/web/showTaskInstances"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockOneTimeService.newOneTimeTask(request)
        model.get("newOneTimeTask").name == request.name
    }

    def "requests to show task instances return view containing both one time and subscribed tasks"() {
        when:
        def response = mockMvc.perform(get("/web/showTaskInstances"))
                .andExpect(status().isOk())
                .andExpect(view().name("showTaskInstancesView"))
                .andExpect(model().attributeExists("tasks", "oneTimeTasks"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        3 * mockUserService.getUser() >> user
        1 * mockSubService.returnTaskInstancesForUser(_) >> taskInstances
        1 * mockOneTimeService.returnOneTimeTaskForUser(_) >> taskOneTimes
        model.size() == 2
        model.get("tasks").get(0).name == taskSubs.get(0).name
        model.get("oneTimeTasks").get(1).name == taskOneTimes.get(1).name
    }

    def "requests to update task completions result in service method calls"() {
        given:
        def subscriptionId = taskInstances.get(0).id
        def instanceId = taskInstances.get(0).taskInstanceId
        def value = 17

        when:
        mockMvc.perform(
                post("/web/tasks/${subscriptionId}/instances/${instanceId}/completions/${value}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> user
        1 * mockSubService.verifyTaskInstance(user, subscriptionId, instanceId) >> true
        1 * mockSubService.returnTaskInstancesForUser(user) >> taskInstances
        1 * mockSubService.updateTaskInstanceCompletions(instanceId, value)
    }

}
