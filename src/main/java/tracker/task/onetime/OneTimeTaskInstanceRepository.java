package tracker.task.onetime;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface OneTimeTaskInstanceRepository extends CrudRepository<OneTimeTaskInstanceEntity, Integer> {
}
