package tracker.task.subscription

import spock.lang.Specification
import spock.lang.Subject
import tracker.task.subscription.*
import tracker.user.UserEntity
import tracker.user.UserRepository
import tracker.user.UserService

import java.time.LocalDate
import java.time.Month

class SubscribedTaskServiceSpec extends Specification {

    @Subject
    SubscribedTaskService service = new SubscribedTaskService()

    UserRepository mockUserRepository = Mock()
    UserService mockUserService = Mock()
    TaskSubscriptionRepository mockSubscriptionRepo = Mock()
    TaskInstanceRepository mockInstanceRepo = Mock()

    def setup() {
        service.userRepository = mockUserRepository
        service.userService = mockUserService
        service.taskSubscriptionRepository = mockSubscriptionRepo
        service.taskInstanceRepository = mockInstanceRepo
    }

    def "newTask adds new task to user and saves"() {
        given:
        def task = new TaskSubscriptionDTO(name: "myTask",
                necessaryCompletions: 1,
                weight: 3,
                period: TaskPeriod.WEEKLY)

        when:
        service.newTask(task)

        then:
        1 * mockUserService.getUser() >> new UserEntity()
        1 * mockUserRepository.save(_)
    }

    def "due date calculator works as expected"() {
        given:
        def taskSubscription = new TaskSubscriptionEntity(id: 1, name: "my task", period: period, weight: 2, taskInstances: [])
        LocalDate.now() >> date

        when:
        def result = service.dueDateForNextInstance(taskSubscription)
        then:
        result == expected

        where:
        period             | date                              | expected
        TaskPeriod.DAILY   | LocalDate.now()                   | LocalDate.now()
        TaskPeriod.WEEKLY  | LocalDate.of(2019, Month.MAY, 24) | LocalDate.of(2019, Month.MAY, 25)
        TaskPeriod.WEEKLY  | LocalDate.of(2019, Month.MAY, 25) | LocalDate.of(2019, Month.MAY, 25)
        TaskPeriod.WEEKLY  | LocalDate.of(2019, Month.MAY, 19) | LocalDate.of(2019, Month.MAY, 25)
        TaskPeriod.MONTHLY | LocalDate.of(2019, Month.MAY, 1)  | LocalDate.of(2019, Month.MAY, 31)
        TaskPeriod.MONTHLY | LocalDate.of(2019, Month.MAY, 31) | LocalDate.of(2019, Month.MAY, 31)
    }

    def "generating task instance saves "() {
        given:
        def task = new TaskSubscriptionEntity(
                name: name,
                period: TaskPeriod.DAILY,
                weight: weight,
                necessaryCompletions: completionsGoal,
                taskInstances: [],
        )
        service.dueDateForNextInstance(_) >> LocalDate.now()

        when:
        service.generateNewInstanceForPeriod(task)

        then:
        1 * mockInstanceRepo.save(_)
        1 * mockSubscriptionRepo.save(_)

        where:
        name            | period           | weight | completionsGoal
        "my daily task" | TaskPeriod.DAILY | 1      | null
    }

}
