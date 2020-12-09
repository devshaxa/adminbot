package uz.softex.adminbot.service;

import org.springframework.stereotype.Service;
import uz.softex.adminbot.model.Employee;
import uz.softex.adminbot.repository.EmployeeRepository;

import java.util.List;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee save(Employee employee){
        return this.employeeRepository.save(employee);
    }

    public Employee getById(Long id){
        return employeeRepository.getBySessionId(id);
    }

    public List<Employee> getAll(){
        return employeeRepository.getByUserIsNotNull();
    }

    public List<Employee> getByStatus(String status){
        return employeeRepository.getByStatus(status);
    }
    public Employee get(Long id){
        return employeeRepository.findById(id).get();
    }
    public void deleteById(Long id){
        employeeRepository.deleteById(id);
    }
}
