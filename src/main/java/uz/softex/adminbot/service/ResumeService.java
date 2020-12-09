package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Resume;
import uz.softex.adminbot.repository.ResumeRepository;

import java.util.List;

@Service
public class ResumeService {

    private ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Resume save(Resume resume){
        return this.resumeRepository.save(resume);
    }

    public Resume getById(Long id){
        return this.resumeRepository.findBySessionId(id);
    }

    public Resume get(Long id){
        return this.resumeRepository.findById(id).get();
    }

    public List<Resume> getAll(){
        return resumeRepository.getByUserIsNotNull();
    }

    public List<Resume> getByStatus(String status){
        return resumeRepository.getByStatus(status);
    }

    public void deleteById(Long id){
        resumeRepository.deleteById(id);
    }
}
