package tracker.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import tracker.task.onetime.OneTimeTaskInstanceEntity;
import tracker.task.subscription.TaskSubscriptionEntity;

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
    @OneToMany(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.SUBSELECT)
    private List<TaskSubscriptionEntity> taskSubscriptions = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.SUBSELECT)
    private List<OneTimeTaskInstanceEntity> oneTimeTaskInstances = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserRolesEntity> userRoles = new ArrayList<>();

}
