package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Job;
import uz.softex.adminbot.repository.JobRepository;

import java.util.List;

@Service
public class JobService {

    private JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job save(Job job){
        return this.jobRepository.save(job);
    }

    public Job getById(Long id){
        return jobRepository.getBySessionId(id);
    }

    public List<Job> getAll(){
        return jobRepository.getByUserIsNotNull();
    }

    public Job get(Long id){
        return jobRepository.findById(id).get();
    }
    public List<Job> getByStatus(String status){
        return jobRepository.getByStatus(status);
    }

    public Job findById(Long id){
        return jobRepository.findById(id).get();
    }

    public void deleteById(Long id){
        jobRepository.deleteById(id);
    }
}
