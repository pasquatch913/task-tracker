package tracker.task.subscription;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface TaskSubscriptionRepository extends CrudRepository<TaskSubscriptionEntity, Integer> {


}
