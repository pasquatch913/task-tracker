package tracker.task;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface OneTimeTaskInstanceRepository extends CrudRepository<OneTimeTaskInstanceEntity, Integer> {
}
