package tracker.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "application_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @OneToMany
    private List<TaskSubscriptionEntity> taskSubscriptions = new ArrayList<>();
    @OneToMany
    private List<OneTimeTaskInstanceEntity> oneTimeTaskInstances = new ArrayList<>();
    @OneToMany
    private List<UserRolesEntity> userRoles = new ArrayList<>();

}
