package tracker.task.mapper

import spock.lang.Specification
import tracker.task.analytics.TaskCompletionEntity
import tracker.task.subscription.TaskInstanceEntity
import tracker.task.subscription.TaskSubscriptionEntity

import java.time.LocalDate
import java.time.LocalDateTime

import static tracker.task.subscription.TaskPeriod.DAILY

class AnalyticsMapperTest extends Specification {

    AnalyticsMapper mapper = new AnalyticsMapper()

    def "Task Subscription to Data"() {
        given:
        def completion1 = new TaskCompletionEntity(id: 2, completionTime: LocalDateTime.now().minusDays(1))
        def completion2 = new TaskCompletionEntity(id: 2, completionTime: LocalDateTime.now())

        def instance = new TaskInstanceEntity(dueAt: LocalDate.now().minusDays(3),
                taskCompletions: [completion1, completion2])
        def subscription = new TaskSubscriptionEntity(id: 1,
                name: "my old task",
                period: DAILY,
                weight: 2,
                necessaryCompletions: 4,
                active: true,
                taskInstances: [instance])


        when:
        def result = mapper.subscriptionToDataPoints(subscription)

        then:
        result.size() == 2
        result.every {
            it ->
                it.name == subscription.name &&
                        it.weight == subscription.weight &&
                        (it.time.toLocalDate() == LocalDate.now() ||
                                it.time.toLocalDate() == LocalDate.now().minusDays(1))
        }
    }
}
