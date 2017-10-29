package pl.com.bottega.hrs.model;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "employees")
public class Employee {

//    private final LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);

    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "enum('M', 'F')")
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;
    @Transient
    private TimeProvider timeProvider;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "emp_no")
    private Collection<Salary> salaries = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "emp_no")
    private Collection<Title> titles = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "emp_no")
    private Collection<DepartmentAssignment> departmentAssigments = new LinkedList<>();

    public Employee() {
    }

    public Employee(Integer empNo, String firstName, String lastName, LocalDate birthDate, Address address,
                    TimeProvider timeProvider) {
        this.empNo = empNo;
        this.address = address;
        this.timeProvider = timeProvider;
        this.hireDate = timeProvider.today();
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateProfile(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }


    public Address getAddress() {
        return address;
    }


    public Collection<Salary> getSalaries() {
        return salaries;
    }

    public Collection<Title> getTitles() {
        return titles;
    }


    public void changeSalary(Integer newSalary) {
        Optional<Salary> optionalSalary = getCurrentSalary();
        if(optionalSalary.isPresent()){
            Salary currentSalary = optionalSalary.get();
            removeOrTerminateSalary(currentSalary);
        }
        addNewSalary(newSalary);
    }

    private void addNewSalary(Integer newSalary) {
        salaries.add(new Salary(empNo, newSalary, timeProvider));
    }

    private void removeOrTerminateSalary(Salary currentSalary) {
        if(currentSalary.startsToday()){
            salaries.remove(currentSalary);
        }
        else {
            currentSalary.terminate();
        }
    }

    private void updateLastSalary() {
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);
        salaries.stream().filter(salary -> salary.isCurrent());
    }

    public Optional<Salary> getCurrentSalary(){
        return salaries.stream().filter(salary -> salary.isCurrent()).findFirst();
    }


/*    public Optional<Integer> getCurrentSalary() {
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);
        for (Salary salary : salaries) {
            if (salary.getToDate().equals(MAX_DATE))
                return Optional.of(salary.getSalary());
        }
        return Optional.empty();
    }*/


    public void assignDepartment(Department department){
        if(!isCurrentlyAssignedTo(department)) {
            departmentAssigments.add(new DepartmentAssignment(empNo, department, timeProvider));
        }
    }

    private boolean isCurrentlyAssignedTo(Department department) {
        return getCurrentDepartments().contains(department);

    }

    public void unassignDepartment(Department department){
        departmentAssigments.stream().
                filter((assignment) -> assignment.isAssigned(department)).
                findFirst().ifPresent(DepartmentAssignment::unassign);
    }

    public Collection<Department> getCurrentDepartments(){
        return departmentAssigments.stream().
                filter(DepartmentAssignment::isCurrent).
                map(DepartmentAssignment::getDepartment).collect(Collectors.toList());
    }


    public void changeTitle(String newTitle) {
        Optional<Title> optionalTitle = getCurrentTitle();
        if(optionalTitle.isPresent()){
            Title currentTitle = optionalTitle.get();
            removeOrTerminateTitle(currentTitle);
        }
        addNewTitle(newTitle);
    }

    private void addNewTitle(String newTitle) {
        titles.add(new Title(empNo, newTitle, timeProvider));
    }

    private void removeOrTerminateTitle(Title currentTitle) {
        if(currentTitle.startsToday()){
            titles.remove(currentTitle);
        }
        else {
            currentTitle.terminate();
        }
    }
/*
    private void addNewTitle(String title) {
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);
        titles.add(new Title(this.getEmpNo(), title, LocalDate.now(), MAX_DATE));
    }

    private void updateLastTitle() {
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);
        titles.stream().filter(title -> title.getToDate().equals(MAX_DATE)).forEach(title -> title.setToDate(LocalDate.now()));
    }*/

    public Optional<Title> getCurrentTitle() {
        return titles.stream().filter(Title::isCurrent).findFirst();
    }



    @Override
    public String toString() {
        return "Employee{" +
                "empNo=" + empNo +
                ", hireDate=" + hireDate +
                ", birthDate=" + birthDate +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", address=" + address +
                ", salaries=" + salaries +
                '}';
    }

    public Collection<DepartmentAssignment> getDepartmentsHistory() {
        return departmentAssigments;
    }
}