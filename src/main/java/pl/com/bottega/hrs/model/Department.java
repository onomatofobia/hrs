package pl.com.bottega.hrs.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "dept_no", columnDefinition = "char(4)")
    String depNo;

    @Column(name = "dept_name")
    String depName;

    public Department() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        return depNo.equals(that.depNo);
    }

    @Override
    public int hashCode() {
        return depNo.hashCode();
    }

    public Department(String depNo, String depName) {
        this.depNo = depNo;
        this.depName = depName;
    }

    public String getDepNo() {
        return depNo;
    }



    public String getDepName() {
        return depName;
    }

    public String getNumber() {
        return depNo;
    }
}