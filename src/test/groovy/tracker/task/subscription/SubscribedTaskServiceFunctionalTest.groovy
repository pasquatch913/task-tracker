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

    def setup() {
        service.userService = mockUserService
    }

    @Transactional
    def "creating a new task results in appropriate artifacts"() {
        given:
        def taskDto = new TaskSubscriptionDTO(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                period: TaskPeriod.WEEKLY)
        def user = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
                taskSubscriptions: [], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])
        userRepository.save(user)

        when:
        service.newTask(taskDto)
        UserEntity resultingUser = userRepository.findByUsername("me").get()

        then:
        1 * mockUserService.getUser() >> user
        subscriptionRepository.findAll().size() == 1
        resultingUser.taskSubscriptions.size() == 1
        println(resultingUser.taskSubscriptions)
    }

}
