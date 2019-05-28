package tracker.task.subscription;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface TaskSubscriptionRepository extends CrudRepository<TaskSubscriptionEntity, Integer> {

    Optional<TaskSubscriptionEntity> findFirstByName(String name);

}
