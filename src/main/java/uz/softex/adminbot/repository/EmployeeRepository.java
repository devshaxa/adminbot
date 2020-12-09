package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    Employee getBySessionId(Long id);
    List<Employee> getByUserIsNotNull();
    List<Employee> getByStatus(String status);
}
