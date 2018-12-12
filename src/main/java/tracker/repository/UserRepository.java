package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.UserEntity;

import javax.transaction.Transactional;

@Transactional
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
}
