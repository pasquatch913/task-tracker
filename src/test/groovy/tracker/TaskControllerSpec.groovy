package tracker

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import tracker.task.TaskController
import tracker.task.TaskService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class TaskControllerSpec extends Specification {

    MockMvc mockMvc

    def setup() {
        def controller = new TaskController()
        def service = new TaskService()
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        controller.taskService = service
    }

    def "root route returns 200"() {
        when:
        def response = mockMvc.perform(get("/")).andReturn().response
        def body = response.content

        then:
        println(response)
        response.status == 200
        body != null
    }

}