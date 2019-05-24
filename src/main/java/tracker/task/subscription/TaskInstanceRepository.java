package tracker.task.subscription;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface TaskInstanceRepository extends CrudRepository<TaskInstanceEntity, Integer> {



}
