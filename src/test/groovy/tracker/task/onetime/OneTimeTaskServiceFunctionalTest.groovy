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
        service.userService = mockUserService
        userRepository.save(user)
    }

    def cleanup() {
        userRepository.deleteAll()
    }

    @Transactional
    def "creating a new one time task results in appropriate artifacts"() {
        given:
        def myNewTask = new OneTimeTaskInstanceEntity(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                dueDate: LocalDate.now())
        UserEntity initialUser = userRepository.findByUsername("me").get()

        when:
        service.newOneTimeTask(myNewTask)

        then:
        1 * mockUserService.getUser() >> initialUser
        UserEntity resultingUser = userRepository.findByUsername("me").get()
        oneTimeTaskInstanceRepository.findAll().size() == 3
        resultingUser.oneTimeTaskInstances.size() == 3
        println(resultingUser.oneTimeTaskInstances)
    }


}
