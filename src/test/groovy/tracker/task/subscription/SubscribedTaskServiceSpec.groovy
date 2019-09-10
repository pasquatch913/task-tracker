package tracker.task.subscription

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tracker.task.analytics.TaskDataPointEntity
import tracker.user.UserEntity
import tracker.user.UserRepository
import tracker.user.UserService
import tracker.web.EntityNotFoundException

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

import static tracker.task.subscription.TaskPeriod.*

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
                period: WEEKLY)

        when:
        service.newTask(task)

        then:
        1 * mockUserService.getUser() >> new UserEntity()
        1 * mockUserRepository.save(_)
    }

    @Unroll
    def "due date calculator works as expected"() {
        given:
        def taskSubscription = new TaskSubscriptionEntity(id: 1, name: "my task", period: period, weight: 2, taskInstances: [])

        when:
        def result = service.dueDateForNextInstance(taskSubscription.period)
        then:
        result == expected

        where:
        period  | expected
        DAILY   | LocalDate.now()
        WEEKLY  | LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        // contrived but *shrug* best i can do right now
        MONTHLY | LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
    }

    def "generating task instance saves "() {
        given:
        def task = new TaskSubscriptionEntity(
                name: name,
                period: DAILY,
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
        name            | period | weight | completionsGoal
        "my daily task" | DAILY  | 1      | 2
    }

    def "getting Active Instances returns list of task instances for active subscriptions only"() {
        given:
        def taskSubscription1 = new TaskSubscriptionEntity(name: "my old task",
                period: DAILY,
                weight: 2,
                necessaryCompletions: 4,
                active: Boolean.FALSE,
                taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(3))])
        def taskSubscription2 = new TaskSubscriptionEntity(name: "my other task",
                period: WEEKLY,
                weight: 2,
                necessaryCompletions: 4,
                taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now().plusDays(3)),
                                new TaskInstanceEntity(dueAt: LocalDate.now().minusMonths(2)),
                                new TaskInstanceEntity(dueAt: LocalDate.now().minusMonths(1))])
        def user = new UserEntity(taskSubscriptions: [taskSubscription1, taskSubscription2])

        when:
        def result = service.getActiveInstances(user)

        then:
        result.size() == 2
    }

    def "updates to subscriptions invoke subscription repository"() {
        given:
        def subscription = new TaskSubscriptionEntity(id: 1,
                name: "my old task",
                period: DAILY,
                weight: 2,
                necessaryCompletions: 4,
                active: true,
                taskInstances: [new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(3))])
        def updateReq = new TaskSubscriptionDTO(id: 1,
                name: name,
                necessaryCompletions: completionsGoal,
                weight: weight,
                period: period,
                active: active)

        when:
        def result = service.updateTask(updateReq)

        then:
        1 * mockSubscriptionRepo.findById(updateReq.id) >> Optional.of(subscription)
        1 * mockSubscriptionRepo.save(_)
        numberInstanceUpdates * mockInstanceRepo.save(_)
        result == expectedResult

        where:
        name          | completionsGoal | weight | period | active || numberInstanceUpdates | expectedResult
        null          | null            | null   | null   | null   || 0                     | new TaskSubscriptionDTO(id: 1, name: "my old task", period: DAILY, weight: 2, necessaryCompletions: 4, active: true)
        ""            | null            | null   | null   | null   || 0                     | new TaskSubscriptionDTO(id: 1, name: "my old task", period: DAILY, weight: 2, necessaryCompletions: 4, active: true)
        null          | null            | null   | null   | false  || 0                     | new TaskSubscriptionDTO(id: 1, name: "my old task", period: DAILY, weight: 2, necessaryCompletions: 4, active: false)
        "my new name" | null            | 57     | null   | null   || 0                     | new TaskSubscriptionDTO(id: 1, name: "my new name", period: DAILY, weight: 57, necessaryCompletions: 4, active: true)
        "all"         | 1               | 1      | WEEKLY | null   || 1                     | new TaskSubscriptionDTO(id: 1, name: "all", period: WEEKLY, weight: 1, necessaryCompletions: 1, active: true)
    }

    def "updates to nonexistent subscriptions throw exception"() {
        given:
        def taskUpdateRequest = new TaskSubscriptionDTO(id: 999,
                name: "my old task",
                necessaryCompletions: 3,
                weight: 2,
                period: MONTHLY,
                active: true)

        when:
        service.updateTask(taskUpdateRequest)

        then:
        1 * mockSubscriptionRepo.findById(taskUpdateRequest.id) >> Optional.empty()
        0 * mockSubscriptionRepo.save(_)
        thrown(EntityNotFoundException)
    }

    def "getting completion data for a user returns a merged list of their completions"() {
        given:
        def user = new UserEntity(id: 1)
        def task1 = new TaskDataPointEntity(name: "cook", time: LocalDateTime.now(), weight: 2)
        def task2 = new TaskDataPointEntity(name: "cook", time: LocalDateTime.now().minusDays(1), weight: 2)
        def task3 = new TaskDataPointEntity(name: "lift", time: LocalDateTime.now(), weight: 3)
        def task4 = new TaskDataPointEntity(name: "lift", time: LocalDateTime.now().minusMinutes(1), weight: 3)
        def queryResult = [task1, task2, task3, task4]

        when:
        def result = service.datapointsForUser(user)

        then:
        1 * mockSubscriptionRepo.findPointsByTimeAndDate(user.id) >> queryResult
        result.size() == 2
    }

}
