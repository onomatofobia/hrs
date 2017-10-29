package pl.com.bottega.hrs.application;

import java.time.LocalDate;
import java.util.Collection;

public class EmployeeSearchCriteria {

    private String lastNameQuery;
    private String firstNameQuery;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;
    private LocalDate hireDateFrom;
    private LocalDate hireDateTo;
    private Integer salaryFrom;
    private Integer salaryTo;
    private Collection<String> titles;
    private Collection<String> departmentNumbers;

    private int pageSize = 20;
    private int pageNumber = 1;



    public String getLastNameQuery() {
        return lastNameQuery;
    }

    public String getFirstNameQuery() {
        return firstNameQuery;
    }

    public LocalDate getBirthDateFrom() {
        return birthDateFrom;
    }

    public LocalDate getBirthDateTo() {
        return birthDateTo;
    }

    public LocalDate getHireDateFrom() {
        return hireDateFrom;
    }

    public LocalDate getHireDateTo() {
        return hireDateTo;
    }

    public Integer getSalaryFrom() {
        return salaryFrom;
    }

    public Integer getSalaryTo() {
        return salaryTo;
    }

    public Collection<String> getTitles() {
        return titles;
    }

    public Collection<String> getDepartmentNumbers() {
        return departmentNumbers;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setLastNameQuery(String lastNameQuery) {
        this.lastNameQuery = lastNameQuery;
    }

    public void setFirstNameQuery(String firstNameQuery) {
        this.firstNameQuery = firstNameQuery;
    }

    public void setBirthDateFrom(LocalDate birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public void setBirthDateTo(LocalDate birthDateTo) {
        this.birthDateTo = birthDateTo;
    }

    public void setHireDateFrom(LocalDate hireDateFrom) {
        this.hireDateFrom = hireDateFrom;
    }

    public void setHireDateTo(LocalDate hireDateTo) {
        this.hireDateTo = hireDateTo;
    }

    public void setSalaryFrom(Integer salaryFrom) {
        this.salaryFrom = salaryFrom;
    }

    public void setSalaryTo(Integer salaryTo) {
        this.salaryTo = salaryTo;
    }

    public void setTitles(Collection<String> titles) {
        this.titles = titles;
    }

    public void setDepartmentNumbers(Collection<String> departmentNumbers) {
        this.departmentNumbers = departmentNumbers;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
