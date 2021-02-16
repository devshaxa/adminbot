package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Resume;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Resume findBySessionId(Long id);
    List<Resume> getByUserIsNotNull();

    @Query("select r from Resume r where r.status=:status order by r.id")
    List<Resume> getByStatus(@Param("status") String status);
}
