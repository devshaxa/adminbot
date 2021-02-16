package uz.softex.adminbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.softex.adminbot.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    Employee getBySessionId(Long id);
    List<Employee> getByUserIsNotNull();

    @Query("select e from Employee e where e.status=:status order by e.id")
    List<Employee> getByStatus(@Param("status") String status);
}
