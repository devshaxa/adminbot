package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Advertisement;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,Long> {

    Advertisement getBySessionId(Long id);

    List<Advertisement> getByUserIsNotNull();

    List<Advertisement> getByStatus(String status);
}
