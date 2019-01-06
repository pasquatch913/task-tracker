package tracker.user;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserDTO {

    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
