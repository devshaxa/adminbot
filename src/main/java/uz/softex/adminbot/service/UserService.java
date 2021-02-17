package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.User;
import uz.softex.adminbot.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean exist(Long userId){
        return this.userRepository.existsById(userId);
    }

    public User getById(Long userId){
        return this.userRepository.findById(userId).get();
    }

    public User save(User user){
        return this.userRepository.save(user);
    }

    public User getBySessionId(Long id){
        return userRepository.getBySessionId(id);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
}
