package pl.com.bottega.hrs.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "dept_emp")
public class DepartmentAssignment {

    private TimeProvider timeProvider;

    public DepartmentAssignment(Integer empNo, Department department, TimeProvider timeProvider) {
        id = new DepartmentAssignmentId(empNo, department);
        this.timeProvider = timeProvider;
        toDate = Constants.MAX_DATE;
        fromDate = timeProvider.today();
    }


    public Department getDepartment() {
        return id.department;
    }

    public boolean isAssigned(Department department) {
        return toDate.isAfter(timeProvider.today()) && department.equals(id.department);
    }

    public void unassign() {
        toDate = timeProvider.today();
    }

    public boolean isCurrent() {
        return toDate.isAfter(timeProvider.today());
    }

    @Embeddable
    public static class DepartmentAssignmentId implements Serializable {
        @Column(name = "emp_no")
        private Integer empNo;
        @ManyToOne
        @JoinColumn(name = "dept_no")
        private Department department;

        public DepartmentAssignmentId(Integer empNo, Department department) {
            this.empNo = empNo;
            this.department = department;
        }

        public DepartmentAssignmentId() {
        }
    }

    @EmbeddedId
    private DepartmentAssignmentId id;
    @Column(name = "from_date")
    private LocalDate fromDate;
    @Column(name = "to_date")
    private LocalDate toDate;


    DepartmentAssignment() {
    }

    public DepartmentAssignment(Integer empNo, Department department, LocalDate fromDate, LocalDate toDate) {
        this.id = new DepartmentAssignmentId(empNo, department);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Department getDept() {
        return id.department;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setDept(Department newDept){
        id.department = newDept;
    }
}
