package tracker.task.subscription


import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.DataFixture
import tracker.user.UserService

import static java.lang.Boolean.TRUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class SubscribedTaskControllerSpec extends Specification {

    MockMvc mockMvc

    UserService mockUserService = Mock()
    SubscribedTaskService mockSubService = Mock()

    @Subject
    SubscribedTaskController controller = new SubscribedTaskController(mockSubService, mockUserService)

    DataFixture data = new DataFixture()

    def baseURL = "/web/subscriptions/"

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    def "requests for the create new subscriptions page return the newTask view"() {
        when:
        def response = mockMvc.perform(get("${baseURL}newTask"))
                .andExpect(status().isOk())
                .andExpect(view().name("newTaskSubscriptionView"))
                .andExpect(model().attributeExists("periods"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        model.get("periods").size() == Arrays.asList(TaskPeriod.values()).size()
    }

    def "requests for the update subscription details page return the update view"() {
        given:
        def taskId = 3
        when:
        def response = mockMvc.perform(get("${baseURL}updateTask/${taskId}"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateTaskSubscriptionView"))
                .andExpect(model().attributeExists("periods"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockSubService.getTaskSubscriptionById(_) >> new TaskSubscriptionDTO(id: taskId)
        model.get("periods").size() == 3
        model.get("subscription").id == taskId
    }

    def "requests to update task subscription details result in service method calls"() {
        given:
        def requestParams = ['id': 1, 'name': "my updated name"]

        when:
        mockMvc.perform(
                post("${baseURL}/updateTask")
                        .param("id", requestParams.get("id").toString())
                        .param("name", requestParams.get("name"))
        )
                .andExpect(status().isFound())
                .andReturn()

        then:
        1 * mockSubService.updateTask(new TaskSubscriptionDTO(id: requestParams.id, name: requestParams.name, active: TRUE))
    }

    def "requests to create new task subscription invoke service methods and redirect"() {
        given:
        def request = new TaskSubscriptionDTO(name: "my new subscription",
                necessaryCompletions: 1, weight: 1, period: TaskPeriod.MONTHLY)

        when:
        def response = mockMvc.perform(post("${baseURL}newTask")
                .flashAttr("newTaskSubscription", request))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/web/showTaskSubscriptions"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockSubService.newTask(request)
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.generateTaskInstances(_)
        model.get("newTaskSubscription").name == request.name
    }

    def "requests to update task completions result in service method calls"() {
        given:
        def instanceId = data.taskInstances().get(0).taskInstanceId
        def value = 17

        when:
        mockMvc.perform(
                post("${baseURL}/instances/${instanceId}/completions/${value}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.verifyTaskInstance(data.user(), instanceId) >> true
        1 * mockSubService.returnTaskInstancesForUser(data.user()) >> data.taskInstances()
        1 * mockSubService.updateTaskInstanceCompletions(instanceId, value)
    }

    def "requests to update task completions don't invoke service methods if user mismatch"() {
        given:
        def instanceId = data.taskInstances().get(0).taskInstanceId
        def value = 17

        when:
        mockMvc.perform(
                post("${baseURL}/instances/${instanceId}/completions/${value}"))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.verifyTaskInstance(data.user(), instanceId) >> false
        0 * mockSubService.returnTaskInstancesForUser(data.user()) >> data.taskInstances()
        0 * mockSubService.updateTaskInstanceCompletions(instanceId, value)
    }

    def "requests to deactivate task succeed if the user owns the task"() {
        given:
        def subscriptionId = data.taskInstances().get(0).id

        when:
        mockMvc.perform(
                post("${baseURL}complete/${subscriptionId}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.unsubscribe(subscriptionId)
    }

    def "requests to deactivate task fail if the user doesn't own the task"() {
        given:
        def subscriptionId = 32

        when:
        mockMvc.perform(
                post("${baseURL}complete/${subscriptionId}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        0 * mockSubService.unsubscribe(subscriptionId)
    }

}
