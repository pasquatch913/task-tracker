package tracker.user;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface UserRolesRepository extends CrudRepository<UserRolesEntity, Integer> {

}
