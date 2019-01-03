package tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tracker.EntityNotFoundException;
import tracker.entity.UserEntity;
import tracker.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public UserEntity getUserFromUsername(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException());
    }

    public UserEntity getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserFromUsername(userDetails);
    }

}
