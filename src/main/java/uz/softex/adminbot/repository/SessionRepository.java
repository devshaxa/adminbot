package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByUserId(Long userId);
}
