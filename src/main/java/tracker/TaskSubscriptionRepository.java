package tracker;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface TaskSubscriptionRepository extends CrudRepository<TaskSubscription, Integer> {


}
