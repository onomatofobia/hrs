package pl.com.bottega.hrs.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "titles")
public class Title {

    private TimeProvider timeProvider;

    public LocalDate getFromDate() {
        return id.fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    @Embeddable
    private static class TitleId implements Serializable{
        @Column(name = "emp_no")
        private Integer empNo;
        private String title;
        private TimeProvider timeProvider;
        @Column(name = "from_date")
        private LocalDate fromDate;

        public TitleId(Integer empNo, String title, TimeProvider timeProvider) {
            this.empNo = empNo;
            this.title = title;
            this.timeProvider = timeProvider;
            this.fromDate = timeProvider.today();
        }

        TitleId() {
        }

        public boolean startsToday() {
            return fromDate.isEqual(timeProvider.today());
        }
    }

    @EmbeddedId
    private TitleId id;

    @Column(name = "to_date")
    private LocalDate toDate;

    Title() {
    }

    public Title(Integer empNo, String title, TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.id = new TitleId(empNo, title, timeProvider);
        this.toDate = Constants.MAX_DATE;
    }


    public boolean isCurrent() {
        return toDate.isAfter(timeProvider.today());
    }

    public boolean startsToday() {

        return id.startsToday();
    }

    public String getValue(){
        return id.title;
    }

    public void terminate() {
        toDate = timeProvider.today();
    }
}

