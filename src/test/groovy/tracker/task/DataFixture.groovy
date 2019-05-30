package tracker.task

import tracker.task.onetime.OneTimeTaskDTO
import tracker.task.onetime.OneTimeTaskInstanceEntity
import tracker.task.subscription.TaskPeriod
import tracker.task.subscription.TaskSubscriptionDTO
import tracker.task.subscription.TaskSubscriptionEntity
import tracker.user.UserEntity
import tracker.user.UserRolesEntity

import java.time.LocalDate

class DataFixture {

    def taskSubs() {
        [new TaskSubscriptionDTO(name: "my first subscription", necessaryCompletions: 2,
                weight: 3, period: TaskPeriod.DAILY),
         new TaskSubscriptionDTO(name: "my second subscription", necessaryCompletions: 3,
                 weight: 2, period: TaskPeriod.WEEKLY),
        ]
    }

    def taskInstances() {
        [new TaskInstanceDTO(id: 3, name: "my first subscription", weight: 3, necessaryCompletions: 2,
                completions: 1, dueDate: LocalDate.now().minusDays(1), taskInstanceId: 11, active: true),
         new TaskInstanceDTO(id: 3, name: "my first subscription", weight: 3, necessaryCompletions: 2,
                 completions: 1, dueDate: LocalDate.now(), taskInstanceId: 12, active: true),
         new TaskInstanceDTO(id: 6, name: "my second subscription", weight: 2, necessaryCompletions: 3,
                 completions: 0, dueDate: LocalDate.now().plusDays(4), taskInstanceId: 13, active: true)]
    }

    def taskOneTimes() {
        [new OneTimeTaskDTO(id: 2, name: "my first one time", weight: 3,
                necessaryCompletions: 6, completions: 2, dueDate: LocalDate.now().plusDays(7)),
         new OneTimeTaskDTO(id: 4, name: "my big one time", weight: 20,
                 necessaryCompletions: 2, completions: 0, dueDate: LocalDate.now().plusDays(60)),
         new OneTimeTaskDTO(id: 5, name: "my last one time", weight: 5,
                 necessaryCompletions: 3, completions: 0, dueDate: LocalDate.now().plusDays(2))]
    }

    def user() {
        new UserEntity(id: 1, username: "me", email: "me@me.com", password: "XXXX",
                taskSubscriptions: [new TaskSubscriptionEntity(id: 3), new TaskSubscriptionEntity(id: 6)],
                oneTimeTaskInstances: [new OneTimeTaskInstanceEntity(id: 2), new OneTimeTaskInstanceEntity(id: 4), new OneTimeTaskInstanceEntity(id: 5)],
                userRoles: [new UserRolesEntity()])
    }


}
