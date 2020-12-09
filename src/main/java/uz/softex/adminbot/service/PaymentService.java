package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Payment;
import uz.softex.adminbot.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {

    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAll(){
        return paymentRepository.findAll();
    }

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }

    public Payment findByAdvertisementId(Long id){
        return paymentRepository.findByAdvertisement_Id(id);
    }

    public Payment findByEmployeeId(Long id){
        return paymentRepository.findByEmployee_Id(id);
    }

    public Payment findByJobId(Long id){
        return paymentRepository.findByJob_Id(id);
    }

    public Payment findByResumeId(Long id){
        return paymentRepository.findByResume_Id(id);
    }

    public List<Payment> getAllByDone(){
        return paymentRepository.findByPayDateNotNull();
    }

}
