package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.TaskInstanceEntity;

import javax.transaction.Transactional;

@Transactional
public interface TaskInstanceRepository extends CrudRepository<TaskInstanceEntity, Integer> {



}
