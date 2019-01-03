package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.UserEntity;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String name);
}
