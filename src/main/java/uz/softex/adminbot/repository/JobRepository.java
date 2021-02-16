package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Job;
import uz.softex.adminbot.model.Session;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    Job getBySessionId(Long id);

    List<Job> getByUserIsNotNull();

    @Query("select j from Job j where j.status=:status order by j.id")
    List<Job> getByStatus(@Param("status") String status);


}
