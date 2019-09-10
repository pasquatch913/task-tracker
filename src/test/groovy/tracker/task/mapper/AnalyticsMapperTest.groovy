package tracker.task.mapper

import spock.lang.Specification
import tracker.task.analytics.TaskDataPointEntity

import java.time.LocalDateTime

class AnalyticsMapperTest extends Specification {

    AnalyticsMapper mapper = new AnalyticsMapper()

    def "Task Subscription to Data"() {
        given:
        def task1 = new TaskDataPointEntity(name: "cook", time: LocalDateTime.now(), weight: 2)
        def task2 = new TaskDataPointEntity(name: "cook", time: LocalDateTime.now().minusDays(1), weight: 2)
        def task3 = new TaskDataPointEntity(name: "lift", time: LocalDateTime.now(), weight: 3)
        def task4 = new TaskDataPointEntity(name: "lift", time: LocalDateTime.now().minusMinutes(1), weight: 3)
        def queryResult = [task1, task2, task3, task4]

        when:
        def result = mapper.taskDataPointEntityToTaskDataPointDTO(queryResult)

        then:
        result.size() == 2
        result.get(0).points == 2
        result.get(1).points == 8
    }
}
