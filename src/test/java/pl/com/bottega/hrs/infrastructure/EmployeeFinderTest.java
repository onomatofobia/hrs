package pl.com.bottega.hrs.infrastructure;

import org.junit.Test;
import pl.com.bottega.hrs.application.*;
import pl.com.bottega.hrs.model.Address;
import pl.com.bottega.hrs.model.Employee;
import pl.com.bottega.hrs.model.StandardTimeProvider;
import pl.com.bottega.hrs.model.TimeProvider;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class EmployeeFinderTest extends InfrastructureTest {

    private int number = 1;
    private final EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    //private EmployeeFinder employeeFinder = new JPQLEmployeeFinder(createEntityManager());
    private EmployeeFinder employeeFinder = new JPACriteriaEmployeeFinder(createEntityManager());
    private EmployeeSearchResults results;

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
        createEmployee("Jan", "Nowak", LocalDate.parse("1960-01-01"));
        createEmployee("Czesław", "Nowacki", LocalDate.parse("1980-03-03"));
        createEmployee("Janusz", "Kowalski", LocalDate.parse("1970-05-05"));

        //when
        criteria.setBirthDateFrom(LocalDate.parse("1971-01-01"));
        search();

        //then
        assertLastNames("Nowacki");

    }

    @Test
    public void shouldFindByBirthDateTo() {

        //given
        createEmployee("Jan", "Nowak", LocalDate.parse("1960-01-01"));
        createEmployee("Czesław", "Nowacki", LocalDate.parse("1980-03-03"));
        createEmployee("Janusz", "Kowalski", LocalDate.parse("1970-05-05"));

        //when
        criteria.setBirthDateTo(LocalDate.parse("1965-01-01"));
        search();

        //then
        assertLastNames("Nowak");
    }

    @Test
    public void shouldFindByBirthDate() {

        //given
        createEmployee("Jan", "Nowak", LocalDate.parse("1960-01-01"));
        createEmployee("Czesław", "Nowacki", LocalDate.parse("1980-03-03"));
        createEmployee("Janusz", "Kowalski", LocalDate.parse("1970-05-05"));

        //when
        criteria.setBirthDateFrom(LocalDate.parse("1971-01-01"));
        criteria.setBirthDateTo(LocalDate.parse("1981-01-01"));
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
        executeInTransaction((em) -> {
                    nowak.changeSalary(50000);
                    em.merge(nowak);
                });
        executeInTransaction((em) -> {
            nowacki.changeSalary(20000);
            em.merge(nowacki);
        });

        criteria.setSalaryFrom(30000);
        criteria.setSalaryTo(200000);
        search();

        assertLastNames("Nowak");
    }

    private Employee createEmployee(String lastName) {
        return createEmployee("Czesiek", lastName);
    }

    private Employee createEmployee(String firstName, String lastName){
        return createEmployee(firstName, lastName, LocalDate.now());

    }

    private Employee createEmployee(String firstName, String lastName, LocalDate birthDate) {
        Address address = new Address("ul. Warszawska 10", "Lublin");
        Employee employee = new Employee(number++, firstName, lastName, birthDate, address, new StandardTimeProvider());

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
}