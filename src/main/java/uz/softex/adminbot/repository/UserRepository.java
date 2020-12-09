package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User getBySessionId(Long id);
}
