package pl.com.bottega.hrs.application;

public class BasicEmployeeDto {

    private String firstName;
    private String lastName;
    private Integer empNo;

    public BasicEmployeeDto(Integer empNo, String firstName, String lastName){
        this.empNo = empNo;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getEmpNo() {
        return empNo;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmpNo(Integer empNo) {
        this.empNo = empNo;
    }
}
