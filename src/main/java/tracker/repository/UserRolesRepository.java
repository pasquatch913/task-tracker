package tracker.repository;

import org.springframework.data.repository.CrudRepository;
import tracker.entity.UserRolesEntity;

import javax.transaction.Transactional;

@Transactional
public interface UserRolesRepository extends CrudRepository<UserRolesEntity, Integer> {

}
