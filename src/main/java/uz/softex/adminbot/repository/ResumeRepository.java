package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Resume;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Resume findBySessionId(Long id);
    List<Resume> getByUserIsNotNull();
    List<Resume> getByStatus(String status);
}
