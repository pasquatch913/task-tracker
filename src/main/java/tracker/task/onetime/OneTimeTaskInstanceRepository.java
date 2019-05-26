package tracker.task.onetime;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface OneTimeTaskInstanceRepository extends JpaRepository<OneTimeTaskInstanceEntity, Integer> {

    List<OneTimeTaskInstanceEntity> findAllByName(String name);
}
