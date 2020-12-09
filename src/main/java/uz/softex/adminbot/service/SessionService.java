package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.*;
import uz.softex.adminbot.repository.SessionRepository;

@Service
public class SessionService {

    private SessionRepository sessionRepository;
    private AdvertisementService advertisementService;
    private JobService jobService;
    private EmployeeService employeeService;
    private UserService userService;
    private HandleService handleService;
    private ResumeService resumeService;

    public SessionService(SessionRepository sessionRepository, AdvertisementService advertisementService, JobService jobService, EmployeeService employeeService, UserService userService, HandleService handleService, ResumeService resumeService) {
        this.sessionRepository = sessionRepository;
        this.advertisementService = advertisementService;
        this.jobService = jobService;
        this.employeeService = employeeService;
        this.userService = userService;
        this.handleService = handleService;
        this.resumeService = resumeService;
    }

    public void save(Session session){
        Employee employee = employeeService.save(session.getEmployee());
        Job job = jobService.save(session.getJob());
        Advertisement advertisement = advertisementService.save(session.getAdvertisement());
        Handle handle = handleService.save(session.getHandle());
        Resume resume = resumeService.save(session.getResume());
        User user = userService.getById(session.getUser().getId());
        Session session1 = new Session();
        session1.setAdvertisement(advertisement);
        session1.setEmployee(employee);
        session1.setJob(job);
        session1.setResume(resume);
        session1.setHandle(handle);
        session1.setUser(user);
        sessionRepository.save(session1);
    }

    public Session getById(Long userId){
        return this.sessionRepository.findByUserId(userId);
    }

    public void update(Session session){
        employeeService.save(session.getEmployee());
        jobService.save(session.getJob());
        advertisementService.save(session.getAdvertisement());
        handleService.save(session.getHandle());
        userService.save(session.getUser());
        resumeService.save(session.getResume());
    }
}
