package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.OneTimeTaskInstanceEntity;

import javax.transaction.Transactional;

@Transactional
public interface OneTimeTaskInstanceRepository extends CrudRepository<OneTimeTaskInstanceEntity, Integer> {
}
