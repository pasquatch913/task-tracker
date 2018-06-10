package tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface TaskRepository extends CrudRepository<TaskEntity, Integer> {

    Optional<List<TaskEntity>> findByUserId(Integer userId);
}
