package pl.com.bottega.hrs;

import pl.com.bottega.hrs.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class App3 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HRS");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.lastName LIKE 'N%' AND e.birthDate > '1960-01-01'" +
                "AND e.birthDate < '1975-01-01'");
        List<Employee> result = query.getResultList();

        for (Employee e : result){
            System.out.println(e.toString());
        }
    }
}
