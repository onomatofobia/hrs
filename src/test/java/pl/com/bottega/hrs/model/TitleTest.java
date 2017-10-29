package pl.com.bottega.hrs.model;


import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.Assert.*;

public class TitleTest {



    private final TimeMachine timeMachine = new TimeMachine();
    private final Address address = new Address("ul. Poziomkowa", "Lublin");
    private final static String TITLE = "Director";
    private static final String ANOTHER_TITLE = "Secretary";
    private static final String YET_ANOTHER_TITLE = "CoffeeMaker";
    private final Employee sut = new Employee(1,
            "Jan",
            "Nowak",
            LocalDate.parse("1960-01-01"),
            address,
            timeMachine);

    @Test
    public void shouldReturnNoTitleIfNoTitleDefined(){

        //then
        assertFalse(getCurrentTitle().isPresent());
    }

    @Test
    public void shouldAddAndReturnEmployeeTitle(){

        //when
        sut.changeTitle(TITLE);

        //then
        assertTrue(getCurrentTitle().isPresent());
        assertEquals(TITLE, getCurrentTitleValue());
    }

    @Test
    public void shouldAllowMultipleChangesOfTitle(){
        //when
        sut.changeTitle(TITLE);
        sut.changeTitle(ANOTHER_TITLE);

        //then
        assertEquals(ANOTHER_TITLE, getCurrentTitleValue());
    }

    @Test
    public void shouldKeepTitleHistory(){
        //when
        timeMachine.travel(Duration.ofDays(- 365 * 2));
        LocalDate t0 = timeMachine.today();
        sut.changeTitle(TITLE);
        timeMachine.travel(Duration.ofDays(365));
        LocalDate t1 = timeMachine.today();
        sut.changeTitle(ANOTHER_TITLE);
        timeMachine.travel(Duration.ofDays(100));
        LocalDate t2 = timeMachine.today();
        sut.changeTitle(YET_ANOTHER_TITLE);

        //then
        Collection<Title> history = sut.getTitles();
        assertEquals(3, history.size());
        assertEquals(Arrays.asList(TITLE, ANOTHER_TITLE, YET_ANOTHER_TITLE),
                history.stream().map((s) -> s.getValue()).collect(Collectors.toList()));

        assertEquals(
                Arrays.asList(t0, t1, t2),
                history.stream().map((s) -> s.getFromDate()).collect(Collectors.toList())
        );

        assertEquals(
                Arrays.asList(t1, t2, TimeProvider.MAX_DATE),
                history.stream().map((s) -> s.getToDate()).collect(Collectors.toList())
        );
    }


    private String getCurrentTitleValue() {
        return getCurrentTitle().get().getValue();
    }

    private Optional<Title> getCurrentTitle() {
        return sut.getCurrentTitle();
    }
}
