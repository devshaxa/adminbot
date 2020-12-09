package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Handle;
import uz.softex.adminbot.repository.HandleRepository;

@Service
public class HandleService {

    private HandleRepository handleRepository;

    public HandleService(HandleRepository handleRepository) {
        this.handleRepository = handleRepository;
    }

    public Handle save(Handle handle){
        return this.handleRepository.save(handle);
    }

    public Handle getById(Long id){
        return handleRepository.getBySessionId(id);


    }
}
