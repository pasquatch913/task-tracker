package tracker.task.onetime;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OneTimeTaskInstanceRepository extends JpaRepository<OneTimeTaskInstanceEntity, Integer> {
}
