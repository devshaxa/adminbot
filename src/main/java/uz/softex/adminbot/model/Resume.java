package uz.softex.adminbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "resume")
public class Resume implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String fathersName;
    private String picUrl;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private String education;
    private String maritalStatus;
    private String purpose;
    private String additionalKnowledge;
    private String jobExperience;
    private String language;
    private String personalProperty;
    private String status;
    private String date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne(mappedBy="resume", cascade = CascadeType.ALL)
    private Session session;

    public Resume() {
    }

    public Resume(String firstname, String lastname, String fathersName, String picUrl, String dateOfBirth, String address, String phone, String email, String education, String maritalStatus, String purpose, String additionalKnowledge, String jobExperience, String language, String personalProperty, String status, String date, User user, Session session) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.fathersName = fathersName;
        this.picUrl = picUrl;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.education = education;
        this.maritalStatus = maritalStatus;
        this.purpose = purpose;
        this.additionalKnowledge = additionalKnowledge;
        this.jobExperience = jobExperience;
        this.language = language;
        this.personalProperty = personalProperty;
        this.status = status;
        this.date = date;
        this.user = user;
        this.session = session;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFathersName() {
        return fathersName;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAdditionalKnowledge() {
        return additionalKnowledge;
    }

    public void setAdditionalKnowledge(String addtionalKnowledge) {
        this.additionalKnowledge = addtionalKnowledge;
    }

    public String getJobExperience() {
        return jobExperience;
    }

    public void setJobExperience(String jobExperience) {
        this.jobExperience = jobExperience;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPersonalProperty() {
        return personalProperty;
    }

    public void setPersonalProperty(String personalProperty) {
        this.personalProperty = personalProperty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
