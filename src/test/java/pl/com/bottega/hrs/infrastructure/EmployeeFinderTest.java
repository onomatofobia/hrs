package pl.com.bottega.hrs.infrastructure;

import org.junit.After;
import org.junit.Test;
import pl.com.bottega.hrs.application.*;
import pl.com.bottega.hrs.model.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class EmployeeFinderTest extends InfrastructureTest {

    private int number = 1;
    private final EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    //private EmployeeFinder employeeFinder = new JPQLEmployeeFinder(createEntityManager());
    private EmployeeFinder employeeFinder = new JPACriteriaEmployeeFinder(createEntityManager());
    private Department d1, d2, d3, d4;
    private EmployeeSearchResults results;
    private TimeMachine timeMachine = new TimeMachine();


    @Test
    public void shouldFindByLastNameQuery() {

        //given
        createEmployee("Nowak");
        createEmployee("Nowacki");
        createEmployee("Kowalski");

        //when
        criteria.setLastNameQuery("nowa");
        search();

        //then
        assertLastNames("Nowak", "Nowacki");
    }


    @Test
    public void shouldFindByFirstNameAndLastNameQuery() {

        //given
        createEmployee("Jan", "Nowak");
        createEmployee("Stefan", "Nowacki");
        createEmployee("Kowalski");

        //when
        criteria.setLastNameQuery("nowa");
        criteria.setFirstNameQuery("ja");
        search();

        //then
        assertLastNames("Nowak");
    }

    @Test
    public void shouldFindByFirstNameQuery() {

        //given
        createEmployee("Nowak");
        createEmployee("Nowacki");
        createEmployee("Kowalski");

        //when
        criteria.setFirstNameQuery("cze");
        search();

        //then
        assertLastNames("Nowak", "Nowacki", "Kowalski");
    }

    @Test
    public void shouldFindByBirthDateFrom() {

        //given
        createEmployee("Jan", "Nowak", "1960-01-01");
        createEmployee("Czesław", "Nowacki", "1980-03-03");
        createEmployee("Janusz", "Kowalski", "1970-05-05");

        //when
        criteria.setBirthDateFrom(LocalDate.parse("1971-01-01"));
        search();

        //then
        assertLastNames("Nowacki");

    }

    @Test
    public void shouldFindByBirthDateTo() {

        //given
        createEmployee("Jan", "Nowak", "1960-01-01");
        createEmployee("Czesław", "Nowacki", "1980-03-03");
        createEmployee("Janusz", "Kowalski", "1970-05-05");

        //when
        criteria.setBirthDateTo(LocalDate.parse("1965-01-01"));
        search();

        //then
        assertLastNames("Nowak");
    }

    @Test
    public void shouldFindByBirthDate() {

        //given
        createEmployee("Jan", "Nowak", "1960-01-01");
        createEmployee("Czesław", "Nowacki", "1980-03-03");
        createEmployee("Janusz", "Kowalski", "1970-05-05");

        //when
        criteria.setBirthDateFrom(LocalDate.parse("1971-01-01"));
        criteria.setBirthDateTo(LocalDate.parse("1981-01-01"));
        search();

        //then
        assertLastNames("Nowacki");
    }

    @Test
    public void shouldFindByHireDate() {

        //given
        timeMachine.travel(Duration.ofDays(-365 * 20));
        createEmployee("Jan", "Nowak", "1960-01-01", timeMachine);
        timeMachine.travel(Duration.ofDays(365*5));
        createEmployee("Czesław", "Nowacki", "1980-03-03", timeMachine);
        timeMachine.travel(Duration.ofDays(365*10));
        createEmployee("Janusz", "Kowalski", "1970-05-05", timeMachine);

        //when
        criteria.setHireDateFrom(LocalDate.parse("2000-01-01"));
        criteria.setHireDateTo(LocalDate.parse("2005-01-01"));
        search();

        //then
        assertLastNames("Nowacki");
    }

    @Test
    public void shouldPaginateResult(){
        //given
        createEmployee("Nowak");
        createEmployee("Nowacki");
        createEmployee("Kowalski");
        createEmployee("Kowalewski");
        createEmployee("Kowalewska");

        //when
        criteria.setPageSize(2);
        criteria.setPageNumber(2);
        criteria.setFirstNameQuery("Cze");
        search();

        //then
        assertLastNames("Kowalski", "Kowalewski");
        assertEquals(5, results.getTotalCount());
        assertEquals(3, results.getPagesCount());
        assertEquals(2, results.getPageNumber());
    }

    @Test
    public void shouldSearchBySalary(){
        //given
        Employee nowak = createEmployee("Nowak");
        Employee nowacki = createEmployee("Nowacki");
        createEmployee("Kowalski");
        nowak.changeSalary(50000);
        executeInTransaction((em) -> em.merge(nowak));
        nowacki.changeSalary(20000);
        executeInTransaction((em) -> em.merge(nowacki));
        criteria.setSalaryFrom(30000);
        criteria.setSalaryTo(200000);
        search();

        assertLastNames("Nowak");
    }

    @Test
    public void shouldSearchBySalaryInTime(){
        //given
        Employee nowak = createEmployee("Jan", "Nowak", "1960-01-01", timeMachine);
        Employee nowacki = createEmployee("Czesław", "Nowacki", "1980-03-03", timeMachine);
        createEmployee("Kowalski");

        timeMachine.travel(Duration.ofDays(-365 * 5));
        changeSalary(nowak, 20000);
        changeSalary(nowacki, 30000);
        timeMachine.travel(Duration.ofDays(365 * 2));
        changeSalary(nowak, 25000);
        changeSalary(nowacki, 35000);
        timeMachine.travel(Duration.ofDays(365));
        changeSalary(nowak, 40000);
        changeSalary(nowacki, 50000);


        criteria.setSalaryFrom(45000);
        criteria.setSalaryTo(60000);
        search();

        assertLastNames("Nowacki");
    }


    @Test
    public void shouldSearchByHistoricalSalary() {
        //given
        employee().withLastName("Nowak").withSalary(50000).create();
        employee().withLastName("Nowacki").withSalary(20000).create();;
        employee().withLastName("Kowalski").withSalary(50000, "1990-01-01").
                withSalary(20000).create();

        //when
        criteria.setSalaryFrom(45000);
        criteria.setSalaryTo(60000);
        search();

        //then
        assertLastNames("Nowak");
    }

    @Test
    public void shouldSearchByTitleInTime(){
        //given
        Employee nowak = createEmployee("Jan", "Nowak", "1960-01-01", timeMachine);
        Employee nowacki = createEmployee("Czesław", "Nowacki", "1980-03-03", timeMachine);


        timeMachine.travel(Duration.ofDays(-365 * 5));
        changeTitle(nowak, "Junior Engineer");
        changeTitle(nowacki, "Junior Developer");
        timeMachine.travel(Duration.ofDays(365 * 2));
        changeTitle(nowak, "Engineer");
        changeTitle(nowacki, "Developer");
        timeMachine.travel(Duration.ofDays(365));
        changeTitle(nowak, "Manager");
        changeTitle(nowacki, "Senior Developer");


        criteria.setTitles(Arrays.asList("Manager"));
        search();

        assertLastNames("Nowak");
    }

    @Test
    public void shouldSearchByDepartments() {
        //given
        createDepartments();
        employee().withLastName("Nowak").withDepartment(d1).create();
        employee().withLastName("Nowacki").withDepartment(d1).withDepartment(d2).create();
        employee().withLastName("Kowalski").withDepartment(d3).create();

        //when
        criteria.setDepartmentNumbers(Arrays.asList(d1.getNumber(), d2.getNumber()));
        search();

        //then
        assertLastNames("Nowak", "Nowacki");
    }

    @Test
    public void shouldSearchByHistoricalDepartments() {
        //given
        createDepartments();
        employee().withLastName("Nowak").withDepartment(d1, "1990-01-01").withoutDepartment(d1).create();
        employee().withLastName("Nowacki").withDepartment(d1).withDepartment(d2).create();
        employee().withLastName("Kowalski").withDepartment(d3).create();

        //when
        criteria.setDepartmentNumbers(Arrays.asList(d1.getNumber(), d2.getNumber()));
        search();

        //then
        assertLastNames("Nowacki");
    }

    @Test
    public void shouldSearchByHistoricalDepartments2() {
        //given
        createDepartments();
        Employee emp1 = employee().withLastName("Nowak").withDepartment(d1, "1960-01-01").withDepartment(d3).withoutDepartment(d1).create();
        Employee emp2 = employee().withLastName("Nowacki").withDepartment(d2, "1960-01-01").withDepartment(d4).create();

        //when
        criteria.setDepartmentNumbers(Arrays.asList(d1.getNumber(), d2.getNumber(), d4.getNumber()));
        search();

        //then
        assertLastNames("Nowacki");
    }

    @Test
    public void np1Demo(){
        //given
        int n = 5;
        for (int i = 1; i<=5; i++){
            employee().withSalary(50000).withLastName("Nowak" + i).create();
        }

        executeInTransaction((entityManager) -> {
            List<Employee> emps = entityManager.createQuery("SELECT e FROM Employee e JOIN FETCH " +
                    "e.salaries JOIN FETCH e.address").getResultList();

            for(Employee e : emps){
                System.out.println(e.getCurrentSalary().get());
            }
        });
    }


    private void changeTitle(Employee employee, String title) {
        executeInTransaction((em) -> {
            employee.changeTitle(title);
            em.merge(employee);
        });
    }

    private void changeSalary(Employee employee, int salary) {
        executeInTransaction((em) -> {
            employee.changeSalary(salary);
            em.merge(employee);
        });
    }

    private Employee createEmployee(String lastName) {

        return createEmployee("Czesiek", lastName);
    }

    private Employee createEmployee(String firstName, String lastName){
        return createEmployee(firstName, lastName, "1980-01-01");

    }

    private Employee createEmployee(String firstName, String lastName, String birthDate){
        return createEmployee(firstName, lastName, birthDate, new StandardTimeProvider());

    }

    private Employee createEmployee(String firstName, String lastName, String birthDate, TimeProvider timeMachine) {
        Address address = new Address("ul. Warszawska 10", "Lublin");
        Employee employee = new Employee(number++, firstName, lastName, LocalDate.parse(birthDate), address, timeMachine);

        executeInTransaction((em) -> em.persist(employee));
        return employee;
    }

    private void search() {
        results = employeeFinder.search(criteria);
    }

    private void assertLastNames(String... lastNames){
        assertLastNames(Arrays.asList(lastNames), results.getResults().stream().
                map(BasicEmployeeDto::getLastName).collect(Collectors.toList()));
    }

    private void assertLastNames(List<String> expected, List<String> collect) {
        assertEquals(expected,
                collect);
    }

    private void createDepartments() {
        d1 = new Department("d1", "Cleaning");
        d2 = new Department("d2", "Marketing");
        d3 = new Department("d3", "Development");
        d4 = new Department("d4", "Development");
        executeInTransaction(em -> {
            em.persist(d1);
            em.persist(d2);
            em.persist(d3);
            em.persist(d4);
        });
    }

    class EmployeeBuilder {

        private String firstName = "Czesiek";
        private String lastName = "Nowak";
        private String birthDate = "1990-01-01";
        private Address address = new Address("al. Warszawska 10", "Lublin");
        private List<Consumer<Employee>> consumers = new LinkedList<>();

        EmployeeBuilder withName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        EmployeeBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        EmployeeBuilder withFirstName(String firstName, String lastName) {
            this.firstName = firstName;
            return this;
        }

        EmployeeBuilder withBirthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        EmployeeBuilder withSalary(Integer salary) {
            consumers.add(employee -> employee.changeSalary(salary));
            return this;
        }

        EmployeeBuilder withSalary(Integer salary, String fromDate) {
            consumers.add(employee -> {
                timeMachine.travel(LocalDate.parse(fromDate));
                employee.changeSalary(salary);
                timeMachine.reset();
            });
            return this;
        }

        EmployeeBuilder withDepartment(Department department){
            consumers.add(employee -> employee.assignDepartment(department));
            return this;
        }

        EmployeeBuilder withDepartment(Department department, String fromDate){
            consumers.add(employee -> {
                timeMachine.travel(LocalDate.parse(fromDate));
                employee.assignDepartment(department);
                timeMachine.reset();
            });
            return this;
        }

        public EmployeeBuilder withoutDepartment(Department department) {
            consumers.add(employee -> employee.unassignDepartment(department));
            return this;
        }


        Employee create() {
            Employee employee = new Employee(number++, firstName, lastName, LocalDate.parse(birthDate), address, timeMachine);
            consumers.forEach(c -> c.accept(employee));
            executeInTransaction((em) -> {
                em.persist(employee);
            });
            return employee;
        }


    }

    private EmployeeBuilder employee() {
        return new EmployeeBuilder();
    }
}