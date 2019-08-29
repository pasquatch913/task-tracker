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
import java.time.LocalDateTime

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
        def myNewTask = new OneTimeTaskDTO(name: "myTask",
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

    def "updates to one time task results in correct completion number and create task completions list"() {
        given:
        def taskToUpdate = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0)

        expect:
        taskToUpdate.taskCompletions.size() == 0

        when:
        def resultTask1 = service.updateOneTimeTaskCompletions(taskToUpdate.id, 3)

        then:
        resultTask1.completions == 3
        def firstTaskCompletionsList = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        firstTaskCompletionsList.size() == 3
        firstTaskCompletionsList.every {
            it ->
                it.completionTime.isBefore(LocalDateTime.now()) &&
                        it.completionTime.isAfter(LocalDateTime.now().minusMinutes(5))
        }

        when:
        def resultTask2 = service.updateOneTimeTaskCompletions(taskToUpdate.id, 1)

        then:
        resultTask2.completions == 1
        def secondTaskCompletionsList = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        secondTaskCompletionsList.size() == 1
        secondTaskCompletionsList.get(0).completionTime.isBefore(LocalDateTime.now()) &&
                secondTaskCompletionsList.get(0).completionTime.isAfter(LocalDateTime.now().minusMinutes(5))

        when:
        def resultTask3 = service.updateOneTimeTaskCompletions(taskToUpdate.id, 1)

        then:
        resultTask3.completions == 1
        def thirdTaskCompletionsList = oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions
        thirdTaskCompletionsList.size() == 1
        thirdTaskCompletionsList.get(0).completionTime.isBefore(LocalDateTime.now()) &&
                thirdTaskCompletionsList.get(0).completionTime.isAfter(LocalDateTime.now().minusMinutes(5))

        when:
        def resultTask4 = service.updateOneTimeTaskCompletions(taskToUpdate.id, -5)

        then:
        resultTask4.completions == 1
        oneTimeTaskInstanceRepository.findAllByName(task1.name).get(0).taskCompletions.size() == 1
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
