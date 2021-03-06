package pl.com.bottega.hrs;

import pl.com.bottega.hrs.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

public class App2 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HRS");

        Address address = new Address("ul. Warszawska", "Lublin");

        Employee employee = new Employee(500008, "Jan", "Sreberko", LocalDate.parse("1960-02-02"), address,
                new StandardTimeProvider());
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(employee);
        em.flush();

        em.getTransaction().commit();
        emf.close();
    }
}
