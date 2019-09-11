package tracker.task.subscription

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
class SubscriptionCompletionServiceTest extends Specification {

    @Autowired
    @Subject
    SubscriptionCompletionService service

    @Autowired
    UserRepository userRepository

    @Autowired
    TaskInstanceRepository taskInstanceRepository

    def task1 = new TaskSubscriptionEntity(name: "my task",
            period: TaskPeriod.DAILY,
            weight: 3,
            necessaryCompletions: 3,
            taskInstances: [
                    new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(1)),
                    new TaskInstanceEntity(dueAt: LocalDate.now())])
    def task2 = new TaskSubscriptionEntity(name: "my new weekly task",
            period: TaskPeriod.WEEKLY,
            weight: 2,
            necessaryCompletions: 4,
            taskInstances: [])
    def task3 = new TaskSubscriptionEntity(name: "my old task",
            period: TaskPeriod.DAILY,
            weight: 2,
            necessaryCompletions: 4,
            taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(3))])
    def task4 = new TaskSubscriptionEntity(name: "my old weekly task",
            period: TaskPeriod.WEEKLY,
            weight: 2,
            necessaryCompletions: 1,
            taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now())])
    def initialUser = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
            taskSubscriptions: [task1, task2, task3, task4], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])


    def setup() {
        userRepository.save(initialUser)
    }

    def cleanup() {
        userRepository.deleteAll()
    }

    def "updates to one time task results in correct completion number and create task completions list"() {
        given:
        def taskToUpdate = taskInstanceRepository.findAll().get(0)

        expect:
        taskToUpdate.taskCompletions.size() == 0

        when:
        service.newTaskInstanceCompletion(taskToUpdate.id)

        then:
        taskInstanceRepository.findById(taskToUpdate.id).get().taskCompletions.size() == 1

        when:
        service.newTaskInstanceCompletion(taskToUpdate.id)

        then:
        def completionsList = taskInstanceRepository.findById(taskToUpdate.id).get().taskCompletions
        completionsList.size() == 2
        completionsList.every {
            it -> it.completionTime.isAfter(LocalDateTime.now().minusSeconds(5))
        }

        when:
        service.newTaskInstanceCompletion(taskToUpdate.id, LocalDateTime.now().minusDays(1))

        then:
        def completionsList2 = taskInstanceRepository.findById(taskToUpdate.id).get().taskCompletions
        completionsList2.size() == 3
        completionsList2.get(2).completionTime.isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)))

        when:
        service.removeTaskCompletion(taskToUpdate.id)

        then:
        def completionsList3 = taskInstanceRepository.findById(taskToUpdate.id).get().taskCompletions
        completionsList3.size() == 2
        completionsList3.every {
            it -> it.completionTime.isAfter(LocalDateTime.now().minusSeconds(5))
        }
    }
}
