package nl.hu.inno.thuusbezorgd.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Component

public class ReportService {

    private final EntityManager entities;

    public ReportService(EntityManager entities) {
        this.entities = entities;
    }

    @Transactional
    public List<OrdersPerDayDTO> generateOrderPerDayReport() {
        //Niets in deze methode is 'hoe het hoort'. Ik wou 'even snel' een rauwe SQL query door Hibernate halen...
        //Dat viel vies tegen:)

        //Dit is een hack om te zorgen dat Postgres niet 'slim kan zijn'. Een echte report-query wint altijd:)
        entities.createNativeQuery("LOCK orders in exclusive mode;").executeUpdate();

        try {
            Thread.sleep(5000); //Een echt rottige reportquery duurt vaak te lang
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> ordersPerDay = entities.createNativeQuery(
                "select extract (day from order_date) as day, extract(month from order_date) as month, extract(year from order_date) as year, count(*) from orders\n" +
                        "group by day,month, year\n" +
                        "order by year, month, day;"
        ).getResultList();

        return ordersPerDay.stream().map(objs -> new OrdersPerDayDTO(
                ((Double) objs[0]).intValue(),
                ((Double) objs[1]).intValue(),
                ((Double) objs[2]).intValue(),
                ((BigInteger) objs[3]).intValue()
        )).toList();
    }

    public record OrdersPerDayDTO(int year, int month, int day, int count) {
    }
}
