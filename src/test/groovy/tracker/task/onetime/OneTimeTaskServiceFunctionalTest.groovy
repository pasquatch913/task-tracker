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

@Transactional
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

    def "creating a new one time task results in appropriate artifacts"() {
        given:
        def myNewTask = new OneTimeTaskInstanceEntity(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                dueDate: LocalDate.now())
        def initialUser = userRepository.findByUsername("me").get()

        when:
        service.newOneTimeTask(myNewTask)

        then:
        1 * mockUserService.getUser() >> initialUser
        def resultingUser = userRepository.findByUsername("me").get()
        oneTimeTaskInstanceRepository.findAll().size() == 3
        resultingUser.oneTimeTaskInstances.size() == 3
        println(resultingUser.oneTimeTaskInstances)
    }

    def "retrieving all one time tasks for a user retuns its tasks"() {
        given:
        def user = userRepository.findByUsername("me").get()

        when:
        def result = service.returnOneTimeTaskForUser(user)

        then:
        result.size() == 2
    }

    def "updates to one time task results in correct completion number"() {
        given:
        def taskToUpdate = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0)

        when:
        def resultTask = service.updateOneTimeTaskCompletions(taskToUpdate.id, value)

        then:
        resultTask.completions == value

        where:
        value << [0, 2, 999, -2]
    }

    def "unsubscribing from a task sets it inactive"() {
        given:
        def taskToUpdate = oneTimeTaskInstanceRepository.findAllByName(task2.name).get(0)

        when:
        service.unsubscribeOneTime(taskToUpdate.id)

        then:
        !oneTimeTaskInstanceRepository.findById(taskToUpdate.id).get().active
    }

}
