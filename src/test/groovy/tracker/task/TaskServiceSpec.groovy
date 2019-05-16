package tracker.task

import spock.lang.Specification
import spock.lang.Subject
import tracker.user.UserEntity
import tracker.user.UserRepository
import tracker.user.UserService

class TaskServiceSpec extends Specification {

    @Subject
    TaskService service = new TaskService()

    UserRepository mockUserRepository = Mock()
    UserService mockUserService = Mock()
    TaskSubscriptionRepository mockSubscriptionRepo = Mock()

    def setup() {
        service.userRepository = mockUserRepository
        service.userService = mockUserService
        service.taskSubscriptionRepository = mockSubscriptionRepo
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
        1 * mockSubscriptionRepo.save(_)
        1 * mockUserRepository.save(_)
    }

}
