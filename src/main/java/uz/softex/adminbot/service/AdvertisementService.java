package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Advertisement;
import uz.softex.adminbot.repository.AdvertisementRepository;

import java.util.List;

@Service
public class AdvertisementService {

    private AdvertisementRepository advertisementRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public Advertisement save(Advertisement advertisement){
        return this.advertisementRepository.save(advertisement);
    }

    public Advertisement getById(Long id){
        return advertisementRepository.getBySessionId(id);
    }

    public Advertisement get(Long id){
        return advertisementRepository.findById(id).get();
    }

    public List<Advertisement> getAll(){
        return advertisementRepository.getByUserIsNotNull();
    }

    public List<Advertisement> getByStatus(String status){
        return advertisementRepository.getByStatus(status);
    }

    public void deleteById(Long id){
        advertisementRepository.deleteById(id);
    }
}
