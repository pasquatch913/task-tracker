package tracker.task.controller

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.DataFixture
import tracker.task.SharedTaskController
import tracker.task.analytics.TaskDataPointDTO
import tracker.task.onetime.OneTimeTaskCompletionService
import tracker.task.onetime.OneTimeTaskService
import tracker.task.subscription.SubscribedTaskService
import tracker.task.subscription.SubscriptionCompletionService
import tracker.user.UserService

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class SharedTaskControllerSpec extends Specification {

    MockMvc mockMvc

    UserService mockUserService = Mock()
    SubscribedTaskService mockSubService = Mock()
    SubscriptionCompletionService mockSubCompService = Mock()
    OneTimeTaskService mockOneTimeService = Mock()
    OneTimeTaskCompletionService mockOneTimeCompService = Mock()

    @Subject
    SharedTaskController controller = new SharedTaskController(mockSubService,
            mockSubCompService,
            mockOneTimeService,
            mockOneTimeCompService,
            mockUserService)

    DataFixture data = new DataFixture()

    def baseURL = "/web/"

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    def "root route returns redirect"() {
        when:
        def response = mockMvc.perform(get(baseURL)).andReturn().response
        def body = response.content

        then:
        println(response)
        response.status == 302
        body != null
    }

    def "requests to show tasks return view containing both one time and subscribed tasks"() {
        when:
        def response = mockMvc.perform(get("${baseURL}showTaskSubscriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("showTaskSubscriptionsView"))
                .andExpect(model().attributeExists("tasks", "oneTimeTasks"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        2 * mockUserService.getUser() >> data.user()
        1 * mockSubService.returnTaskSubscriptionsForUser(_) >> data.taskSubs()
        1 * mockOneTimeService.returnOneTimeTaskForUser(_) >> data.taskOneTimes()
        model.size() == 2
        model.get("tasks").get(0).name == data.taskSubs().get(0).name
        model.get("oneTimeTasks").get(1).name == data.taskOneTimes().get(1).name
    }

    def "requests to show task instances return view containing both one time and subscribed tasks"() {
        when:
        def response = mockMvc.perform(get("${baseURL}showTaskInstances"))
                .andExpect(status().isOk())
                .andExpect(view().name("showTaskInstancesView"))
                .andExpect(model().attributeExists("tasks", "oneTimeTasks"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        3 * mockUserService.getUser() >> data.user()
        1 * mockSubService.returnTaskInstancesForUser(_) >> data.taskInstances()
        1 * mockOneTimeService.returnOneTimeTaskForUser(_) >> data.taskOneTimes()
        model.size() == 2
        model.get("tasks").get(0).name == data.taskSubs().get(0).name
        model.get("oneTimeTasks").get(1).name == data.taskOneTimes().get(1).name
    }

    def "request for the analytics view shows the correct page"() {
        when:
        def response = mockMvc.perform(get("${baseURL}taskData"))
                .andExpect(status().isOk())
                .andExpect(view().name("showTaskDataView"))
                .andExpect(model().attributeExists("subscriptionData"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.datapointsForUser(_) >> [new TaskDataPointDTO(date: LocalDate.now().minusDays(1), points: 1), new TaskDataPointDTO(date: LocalDate.now(), points: 2)]
        model.size() == 1
        model.get("subscriptionData").get(0).points == 1
        model.get("subscriptionData").get(0).date == LocalDate.now().minusDays(1)
    }

    def "requests to update task completions result in service method calls"() {
        given:
        def instanceId = data.taskInstances().get(0).taskInstanceId
        def value = 17

        when:
        mockMvc.perform(
                post("${baseURL}/tasks/complete/${instanceId}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.verifyTaskInstance(data.user(), instanceId) >> true
        1 * mockSubService.returnTaskInstancesForUser(data.user()) >> data.taskInstances()
        1 * mockSubCompService.newTaskInstanceCompletion(instanceId)
    }

    def "requests to update task completions don't invoke service methods if user mismatch"() {
        given:
        def instanceId = data.taskInstances().get(0).taskInstanceId
        def value = 17

        when:
        mockMvc.perform(
                post("${baseURL}/tasks/complete/${instanceId}"))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockSubService.verifyTaskInstance(data.user(), instanceId) >> false
        0 * mockSubService.returnTaskInstancesForUser(_)
        0 * mockSubCompService.newTaskInstanceCompletion(_)

        and:
        1 * mockOneTimeService.verifyOneTimeTask(data.user(), instanceId) >> false
        0 * mockOneTimeService.returnOneTimeTaskForUser(_)
        0 * mockOneTimeCompService.newTaskCompletion(_)
    }

}
