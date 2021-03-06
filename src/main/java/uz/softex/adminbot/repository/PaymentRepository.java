package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Payment findByAdvertisement_Id(Long id);

    Payment findByEmployee_Id(Long id);

    Payment findByJob_Id(Long id);

    Payment findByResume_Id(Long id);

    List<Payment> findByPayDateNotNull();

    @Query("select p from Payment p where p.date=:date")
    List<Payment> findByDate(@Param("date") String date);
}
