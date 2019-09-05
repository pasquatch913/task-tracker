package tracker.task.mapper;

import tracker.task.subscription.TaskInstanceEntity;

import java.util.Comparator;

public class TaskInstanceComparator implements Comparator<TaskInstanceEntity> {

    public int compare(TaskInstanceEntity task1, TaskInstanceEntity task2) {
        if (task1.getDueAt().isAfter(task2.getDueAt())) {
            return 1;
        }
        if (task1.getDueAt().isBefore(task2.getDueAt())) {
            return -1;
        } else return 0;
    }
}
