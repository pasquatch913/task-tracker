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
import java.time.LocalDate

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
    TaskInstanceRepository instanceRepository

    @Autowired
    UserRepository userRepository

    UserService mockUserService = Mock()

    def task1 = new TaskSubscriptionEntity(name: "my task",
            period: TaskPeriod.DAILY,
            weight: 3,
            necessaryCompletions: 3,
            taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now())])
    def task2 = new TaskSubscriptionEntity(name: "my weekly task",
            period: TaskPeriod.WEEKLY,
            weight: 2,
            necessaryCompletions: 4,
            taskInstances: [])
    def task3 = new TaskSubscriptionEntity(name: "my old task",
            period: TaskPeriod.DAILY,
            weight: 2,
            necessaryCompletions: 4,
            taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(3))])
    def initialUser = new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
            taskSubscriptions: [task1, task2, task3], oneTimeTaskInstances: [], userRoles: [new UserRolesEntity()])

    def setup() {
        service.userService = mockUserService
        userRepository.save(initialUser)
    }

    def cleanup() {
        userRepository.deleteAll()
    }

    def "creating a new task results in appropriate artifacts"() {
        given:
        def taskDto = new TaskSubscriptionDTO(name: "myTask",
                necessaryCompletions: 1,
                weight: 2,
                period: TaskPeriod.WEEKLY)
        def user = userRepository.findByUsername("me").get()

        when:
        service.newTask(taskDto)
        def resultingUser = userRepository.findByUsername("me").get()

        then:
        1 * mockUserService.getUser() >> user
        subscriptionRepository.findAll().size() == 4
        resultingUser.taskSubscriptions.size() == 4
        println(resultingUser.taskSubscriptions)
    }

    def "generating new instances works as expected"() {
        given:
        def user = userRepository.findByUsername("me").get()

        when:
        service.generateTaskInstances(user)
        def userSubscriptions = userRepository.findByUsername("me").get().getTaskSubscriptions()

        then:
        userSubscriptions.get(0).taskInstances.get(0).dueAt == LocalDate.now()
        userSubscriptions.get(1).taskInstances.get(0).dueAt >= LocalDate.now()
        userSubscriptions.get(2).taskInstances.get(1).dueAt == LocalDate.now()
        instanceRepository.findAll().size() == 4
    }

    def "retrieving task subscriptions works as expected"() {
        given:
        def user = userRepository.findByUsername("me").get()

        when:
        def result = service.returnTaskSubscriptionsForUser(user)

        then:
        result.get(0).name == task1.name
        result.get(1).name == task2.name
        result.get(2).name == task3.name
        instanceRepository.findAll().size() == 2
    }

    def "retrieving task instances works as expected"() {
        given:
        def user = userRepository.findByUsername("me").get()

        when:
        def result = service.returnTaskInstancesForUser(user)

        then:
        //new tasks generated
        instanceRepository.findAll().size() == 4
        // only 1 instance returned for each subscription
        result.findAll { n -> n.name == task1.name }.size() == 1
        result.findAll { n -> n.name == task2.name }.size() == 1
        result.findAll { n -> n.name == task3.name }.size() == 1
    }

    def "unsubscribing from task by ID results in inactive subscription"() {
        given:
        def user = userRepository.findByUsername("me").get()

        when:
        service.unsubscribe(user.taskSubscriptions.get(0).id)
        def resultingUser = userRepository.findByUsername("me").get()

        then:
        !resultingUser.taskSubscriptions.get(0).active
    }

    def "updating task instance completions changes the task completion count"() {
        given:
        def taskInstanceToUpdate = subscriptionRepository.findFirstByName(task1.name).get().taskInstances.get(0)

        when:
        service.updateTaskInstanceCompletions(taskInstanceToUpdate.id, value)

        then:
        instanceRepository.findById(taskInstanceToUpdate.id).get().completions == value

        where:
        value << [3, 300, -3, 0]
    }

}
