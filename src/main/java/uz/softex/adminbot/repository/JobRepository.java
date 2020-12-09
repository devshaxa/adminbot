package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Job;
import uz.softex.adminbot.model.Session;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    Job getBySessionId(Long id);

    List<Job> getByUserIsNotNull();
    List<Job> getByStatus(String status);


}
