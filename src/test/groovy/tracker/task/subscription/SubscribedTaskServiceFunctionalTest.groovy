package tracker.task.subscription

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Subject
import tracker.task.subscription.SubscribedTaskService
import tracker.task.subscription.TaskPeriod
import tracker.task.subscription.TaskSubscriptionDTO
import tracker.task.subscription.TaskSubscriptionRepository
import tracker.user.UserEntity
import tracker.user.UserRepository
import tracker.user.UserRolesEntity
import tracker.user.UserService

import javax.transaction.Transactional

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY

@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = ANY)
@SpringBootTest
class SubscribedTaskServiceFunctionalTest extends Specification {

    @Autowired
    @Subject
    SubscribedTaskService service

    @Autowired
    TaskSubscriptionRepository subscriptionRepository

    @Autowired
    UserRepository userRepository

    UserService mockUserService = Mock()

    def task1 = new TaskSubscriptionEntity(name: "my task",
            period: TaskPeriod.DAILY,
            weight: 3,
            necessaryCompletions: 3,
            taskInstances: [])
    def task2 = new TaskSubscriptionEntity(name: "my task",
            period: TaskPeriod.WEEKLY,
            weight: 2,
            necessaryCompletions: 4,
            taskInstances: [])
    def initialUser = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
            taskSubscriptions: [task1, task2], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])

    def setup() {
        service.userService = mockUserService
        userRepository.save(initialUser)
    }

    def "creating a new task results in appropriate artifacts"() {
        given:
        def taskDto = new TaskSubscriptionDTO(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                period: TaskPeriod.WEEKLY)
        UserEntity user = userRepository.findByUsername("me").get()

        when:
        service.newTask(taskDto)
        UserEntity resultingUser = userRepository.findByUsername("me").get()

        then:
        1 * mockUserService.getUser() >> user
        subscriptionRepository.findAll().size() == 3
        resultingUser.taskSubscriptions.size() == 3
        println(resultingUser.taskSubscriptions)
    }

    def "generating new instances works as expected"() {
        given:
        UserEntity user = userRepository.findByUsername("me").get()

        when:
        service.generateTaskInstances(user)
        UserEntity resultingUser = userRepository.findByUsername("me").get()

        then:
        resultingUser.taskSubscriptions.every {
            it -> it.taskInstances.size() == 1
        }
    }

}
