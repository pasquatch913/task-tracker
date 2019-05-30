package tracker.task.controller

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.DataFixture
import tracker.task.SharedTaskController
import tracker.task.onetime.OneTimeTaskService
import tracker.task.subscription.SubscribedTaskService
import tracker.user.UserService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class SharedTaskControllerSpec extends Specification {

    MockMvc mockMvc

    UserService mockUserService = Mock()
    SubscribedTaskService mockSubService = Mock()
    OneTimeTaskService mockOneTimeService = Mock()

    @Subject
    SharedTaskController controller = new SharedTaskController(mockSubService, mockOneTimeService, mockUserService)

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

}
