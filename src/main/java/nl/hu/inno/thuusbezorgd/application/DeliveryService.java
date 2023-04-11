package nl.hu.inno.thuusbezorgd.application;

import nl.hu.inno.thuusbezorgd.data.DeliveryRepository;
import nl.hu.inno.thuusbezorgd.data.RiderRepository;
import nl.hu.inno.thuusbezorgd.domain.Delivery;
import nl.hu.inno.thuusbezorgd.domain.Order;
import nl.hu.inno.thuusbezorgd.domain.OrderStatus;
import nl.hu.inno.thuusbezorgd.domain.Rider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class DeliveryService {

    private final RiderRepository riders;
    private final DeliveryRepository deliveries;

    public DeliveryService(RiderRepository riders, DeliveryRepository deliveries) {
        this.riders = riders;
        this.deliveries = deliveries;
    }

    @Transactional
    public Delivery scheduleDelivery(Order order) {
        List<Rider> riders = this.riders.findAll();
        // Dit haalt alle riders uit de database EN alle deliveries, en gaat dan pas kijken wie er ruimte heeft.
        // Dat kan vast handiger!
        Optional<Rider> withLeastDeliveries = riders.stream().min(Comparator.comparingInt(Rider::getNrOfDeliveries));

        if (withLeastDeliveries.isPresent()) {
            Delivery newDelivery = new Delivery(order, withLeastDeliveries.get());
            deliveries.save(newDelivery);
            return newDelivery;
        } else {
            //Dit is natuurlijk fraaier met een custom exception type
            throw new RuntimeException("No available rider found");
        }
    }

    public int getMinutesRemaining(Delivery delivery) {
        if (delivery.getOrder().getStatus() == OrderStatus.Underway) {
            // We gebruiken uiteraard complexe geografische informatie om de resterende tijd te schatten
            // Net als Windows installers!
            return (int) (Math.random() * 100);
        } else {
            return -1;
        }


    }
}
