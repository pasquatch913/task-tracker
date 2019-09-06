package tracker.task.onetime

import spock.lang.Specification
import spock.lang.Subject
import tracker.web.EntityNotFoundException

import java.time.LocalDate

import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE

class OneTimeTaskServiceSpec extends Specification {

    @Subject
    OneTimeTaskService service = new OneTimeTaskService()

    OneTimeTaskInstanceRepository mockOneTimeTaskRepository = Mock()

    def setup() {
        service.oneTimeTaskInstanceRepository = mockOneTimeTaskRepository
    }

    def "updates to one time tasks trigger repository method"() {
        given:
        def task = new OneTimeTaskInstanceEntity(id: 1,
                name: "my task",
                dueDate: LocalDate.now(),
                weight: 2,
                necessaryCompletions: 4,
                active: true)
        def updateRequest = new OneTimeTaskDTO(id: 1,
                name: name,
                dueDate: dueDate,
                weight: weight,
                necessaryCompletions: completionsGoal,
                active: active)

        when:
        def result = service.updateOneTimeTask(updateRequest)

        then:
        1 * mockOneTimeTaskRepository.findById(task.id) >> Optional.of(task)
        1 * mockOneTimeTaskRepository.save(_)
        result == expectedResult

        where:
        where:
        name          | completionsGoal | weight | dueDate                     | active || expectedResult
        null          | null            | null   | null                        | null   || new OneTimeTaskDTO(id: 1, name: "my task", weight: 2, necessaryCompletions: 4, dueDate: LocalDate.now(), active: TRUE)
        ""            | null            | null   | null                        | null   || new OneTimeTaskDTO(id: 1, name: "my task", weight: 2, necessaryCompletions: 4, dueDate: LocalDate.now(), active: TRUE)
        null          | null            | null   | null                        | false  || new OneTimeTaskDTO(id: 1, name: "my task", weight: 2, necessaryCompletions: 4, dueDate: LocalDate.now(), active: FALSE)
        "my new name" | null            | null   | null                        | null   || new OneTimeTaskDTO(id: 1, name: "my new name", weight: 2, necessaryCompletions: 4, dueDate: LocalDate.now(), active: TRUE)
        "all"         | 1               | 1      | LocalDate.now().plusDays(7) | null   || new OneTimeTaskDTO(id: 1, name: "all", weight: 1, necessaryCompletions: 1, dueDate: LocalDate.now().plusDays(7), active: TRUE)
    }

    def "updates to nonexistent subscriptions throw exception"() {
        given:
        def taskUpdateRequest = new OneTimeTaskDTO(id: 999,
                name: "my task",
                necessaryCompletions: 3,
                weight: 2,
                dueDate: LocalDate.now(),
                active: true)

        when:
        service.updateOneTimeTask(taskUpdateRequest)

        then:
        1 * mockOneTimeTaskRepository.findById(taskUpdateRequest.id) >> Optional.empty()
        0 * mockOneTimeTaskRepository.save(_)
        thrown(EntityNotFoundException)
    }

}
