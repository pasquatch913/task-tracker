package tracker.task.onetime

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.DataFixture
import tracker.user.UserService

import java.time.LocalDate

import static java.lang.Boolean.TRUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class OneTimeTaskControllerSpec extends Specification {

    MockMvc mockMvc

    UserService mockUserService = Mock()
    OneTimeTaskService mockOneTimeService = Mock()

    @Subject
    OneTimeTaskController controller = new OneTimeTaskController(mockOneTimeService, mockUserService)

    DataFixture data = new DataFixture()

    def baseURL = "/web/oneTimes/"

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }


    def "requests for the create one time task page return appropriate view"() {
        when:
        def response = mockMvc.perform(get("${baseURL}newTask"))
                .andExpect(status().isOk())
                .andExpect(view().name("newOneTimeTaskView"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        model.size() == 0
    }

    def "requests to create new one time task invoke service methods and redirect"() {
        given:
        def request = new OneTimeTaskDTO(id: 999, name: "my new one time task",
                necessaryCompletions: 1, weight: 1, dueDate: LocalDate.MAX)

        when:
        def response = mockMvc.perform(post("${baseURL}newTask")
                .flashAttr("newOneTimeTask", request))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/web/showTaskInstances"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockOneTimeService.newOneTimeTask(request)
        model.get("newOneTimeTask").name == request.name
    }

    def "requests for the update subscription details page return the update view"() {
        given:
        def taskId = 3
        when:
        def response = mockMvc.perform(get("${baseURL}updateTask/${taskId}"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateOneTimeTaskView"))
                .andReturn()
        def model = response.modelAndView.model

        then:
        1 * mockOneTimeService.getOneTimeTaskById(_) >> new OneTimeTaskDTO(id: taskId)
        model.get("task").id == taskId
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
        1 * mockOneTimeService.updateOneTimeTask(new OneTimeTaskDTO(id: requestParams.id, name: requestParams.name, active: TRUE))
    }

    def "requests to complete one time task result in service method calls"() {
        given:
        def taskId = data.taskOneTimes().get(0).id

        when:
        mockMvc.perform(
                post("${baseURL}complete/${taskId}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        1 * mockOneTimeService.unsubscribeOneTime(taskId)
    }

    def "requests to complete one time task executes no service calls if the user doesn't own task"() {
        given:
        def taskId = 37

        when:
        mockMvc.perform(
                post("${baseURL}complete/${taskId}"))
                .andExpect(status().isAccepted())
                .andReturn()

        then:
        1 * mockUserService.getUser() >> data.user()
        0 * mockOneTimeService.unsubscribeOneTime(taskId)
    }
}
