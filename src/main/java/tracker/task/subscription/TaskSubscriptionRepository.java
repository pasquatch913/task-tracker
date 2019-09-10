package tracker.task.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracker.task.analytics.TaskDataPointEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface TaskSubscriptionRepository extends JpaRepository<TaskSubscriptionEntity, Integer> {

    Optional<TaskSubscriptionEntity> findFirstByName(String name);

    @Query(value = "SELECT new tracker.task.analytics.TaskDataPointEntity(sub.name, comp.completionTime, sub.weight) FROM UserEntity u " +
            "JOIN u.taskSubscriptions sub " +
            "JOIN sub.taskInstances inst " +
            "LEFT JOIN inst.taskCompletions comp " +
            "WHERE comp.completionTime IS NOT NULL AND u.id = :userId")
    List<TaskDataPointEntity> findPointsByTimeAndDate(@Param("userId") Integer userId);
}
