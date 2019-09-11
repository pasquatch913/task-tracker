package tracker.task.onetime

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Subject
import tracker.user.UserEntity
import tracker.user.UserRepository
import tracker.user.UserRolesEntity

import javax.transaction.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY

@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = ANY)
@SpringBootTest
class OneTimeTaskCompletionServiceTest extends Specification {

    @Autowired
    @Subject
    OneTimeTaskCompletionService service

    @Autowired
    UserRepository userRepository

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository

    def task1 = new OneTimeTaskInstanceEntity(name: "my first task",
            necessaryCompletions: 1,
            weight: 2,
            dueDate: LocalDate.now().plusDays(4))
    def task2 = new OneTimeTaskInstanceEntity(name: "my second task",
            necessaryCompletions: 1,
            weight: 2,
            dueDate: LocalDate.now().plusDays(2))
    def user = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
            taskSubscriptions: [], oneTimeTaskInstances: [task1, task2], userRoles: [new UserRolesEntity()])

    def setup() {
        userRepository.save(user)
    }

    def cleanup() {
        userRepository.deleteAll()
    }

    def "updates to one time task results in correct completion number and create task completions list"() {
        given:
        def taskToUpdate = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0)

        expect:
        taskToUpdate.taskCompletions.size() == 0

        when:
        service.newTaskCompletion(taskToUpdate.id)

        then:
        oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions.size() == 1

        when:
        service.newTaskCompletion(taskToUpdate.id)

        then:
        def completionsList = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        completionsList.size() == 2
        completionsList.every {
            it -> it.completionTime.isAfter(LocalDateTime.now().minusSeconds(5))
        }

        when:
        service.newTaskCompletion(taskToUpdate.id, LocalDateTime.now().minusDays(1))

        then:
        def completionsList2 = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        completionsList2.size() == 3
        completionsList2.get(2).completionTime.isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)))

        when:
        service.removeTaskCompletion(taskToUpdate.id)

        then:
        def completionsList3 = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        completionsList3.size() == 2
        completionsList3.every {
            it -> it.completionTime.isAfter(LocalDateTime.now().minusSeconds(5))
        }
    }
}
