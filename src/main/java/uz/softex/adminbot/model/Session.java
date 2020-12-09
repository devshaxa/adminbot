package uz.softex.adminbot.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "session")
public class Session implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Job job;

    @OneToOne(fetch = FetchType.EAGER)
    private Employee employee;

    @OneToOne(fetch = FetchType.EAGER)
    private Advertisement advertisement;

    @OneToOne(fetch = FetchType.EAGER)
    private Handle handle;

    @OneToOne(fetch = FetchType.EAGER)
    private Resume resume;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public Session() {
    }

    public Session(Job job, Employee employee, Advertisement advertisement, Handle handle, Resume resume, User user) {
        this.job = job;
        this.employee = employee;
        this.advertisement = advertisement;
        this.handle = handle;
        this.resume = resume;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public Handle getHandle() {
        return handle;
    }

    public void setHandle(Handle handle) {
        this.handle = handle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
