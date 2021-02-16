package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Advertisement;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,Long> {

    Advertisement getBySessionId(Long id);

    List<Advertisement> getByUserIsNotNull();

    @Query("select a from Advertisement a where a.status=:status order by a.id")
    List<Advertisement> getByStatus(@Param("status") String status);
}
