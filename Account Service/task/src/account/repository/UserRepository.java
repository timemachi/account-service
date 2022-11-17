package account.repository;

import account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);

    public boolean existsByUsername(String username);

    default public boolean isAdministrator(User user) {
        return user.getUserGroups().stream().anyMatch(group -> group.getName().equals("ROLE_ADMINISTRATOR"));
    }
}
