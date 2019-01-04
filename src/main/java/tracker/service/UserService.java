package tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tracker.EntityNotFoundException;
import tracker.entity.UserDTO;
import tracker.entity.UserEntity;
import tracker.entity.UserRolesEntity;
import tracker.repository.UserRepository;
import tracker.repository.UserRolesRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRolesRepository userRolesRepository;

    public UserEntity getUserFromUsername(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException());
    }

    public UserEntity getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserFromUsername(userDetails);
    }

    public Boolean createUser(UserDTO userDTO) {
        // code smell. come up with more elegant way to handle success/failure
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return Boolean.FALSE;
        } else {
            UserRolesEntity userRolesEntity = new UserRolesEntity();
            userRolesRepository.save(userRolesEntity);

            UserEntity newUser = new UserEntity();
            newUser.setUsername(userDTO.getUsername());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            newUser.getUserRoles().add(userRolesEntity);
            userRepository.save(newUser);
            return Boolean.TRUE;
        }
    }
}
