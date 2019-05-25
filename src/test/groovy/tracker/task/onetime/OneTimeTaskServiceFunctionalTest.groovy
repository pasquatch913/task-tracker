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
import tracker.user.UserService

import javax.transaction.Transactional
import java.time.LocalDate

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY

@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = ANY)
@SpringBootTest
class OneTimeTaskServiceFunctionalTest extends Specification {

    @Autowired
    @Subject
    OneTimeTaskService service

    @Autowired
    UserRepository userRepository

    @Autowired
    OneTimeTaskInstanceRepository oneTimeTaskInstanceRepository

    UserService mockUserService = Mock()

    def setup() {
        service.userService = mockUserService
    }

    @Transactional
    def "creating a new task results in appropriate artifacts"() {
        given:
        def taskEntity = new OneTimeTaskInstanceEntity(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                dueDate: LocalDate.now())
        def user = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
                taskSubscriptions: [], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])
        userRepository.save(user)

        when:
        service.newOneTimeTask(taskEntity)
        UserEntity resultingUser = userRepository.findByUsername("me").get()

        then:
        1 * mockUserService.getUser() >> user
        oneTimeTaskInstanceRepository.findAll().size() == 1
        resultingUser.oneTimeTaskInstances.size() == 1
        println(resultingUser.oneTimeTaskInstances)
    }

}
