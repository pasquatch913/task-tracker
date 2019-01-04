package tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tracker.DuplicateEntityException;
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

    public void createUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new DuplicateEntityException();
        } else {
            UserRolesEntity userRolesEntity = new UserRolesEntity();
            userRolesRepository.save(userRolesEntity);

            UserEntity newUser = new UserEntity();
            newUser.setUsername(userDTO.getUsername());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
            newUser.getUserRoles().add(userRolesEntity);
            userRepository.save(newUser);
        }
    }
}
