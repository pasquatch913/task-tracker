package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.TaskSubscriptionEntity;

import javax.transaction.Transactional;

@Transactional
public interface TaskSubscriptionRepository extends CrudRepository<TaskSubscriptionEntity, Integer> {


}
