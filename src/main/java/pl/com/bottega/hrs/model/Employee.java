package pl.com.bottega.hrs.model;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

@Entity
@Table(name = "employees")
public class Employee{

    public Employee(Integer empNo, String firstName, String lastName, LocalDate birthDate, Address address, Gender gender) {
        this.empNo = empNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        this.hireDate = LocalDate.now();
    }

    public Employee(){}

    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Enumerated(value= EnumType.STRING)
    @Column(columnDefinition = "enum('M', 'F')")
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "emp_no")
    private Collection<Salary> salaries = new LinkedList<>();


    public void updateProfile(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public void updateFirstName(String newName, Employee employee){
        employee.updateProfile(newName, "Nowak", LocalDate.now());
    }

    public Address getAddress() {
        return address;
    }

    public String getFirstName() {
        return firstName;
    }

    public Collection<Salary> getSalaries() {
        return salaries;
    }

    @Override
    public String toString() {
        return firstName +
                " | " + lastName + " | " +
                " birthDate: " + birthDate;
    }
}
