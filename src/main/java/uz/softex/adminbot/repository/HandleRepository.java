package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Handle;

@Repository
public interface HandleRepository extends JpaRepository<Handle,Long> {

    Handle getBySessionId(Long id);
}
