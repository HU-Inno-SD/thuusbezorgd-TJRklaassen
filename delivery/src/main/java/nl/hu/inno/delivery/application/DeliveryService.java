package nl.hu.inno.delivery.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import nl.hu.inno.delivery.data.DeliveryRepository;
import nl.hu.inno.delivery.data.RiderRepository;
import nl.hu.inno.delivery.domain.Delivery;
import nl.hu.inno.delivery.domain.DeliveryOrder;
import nl.hu.inno.delivery.domain.Rider;
import nl.hu.inno.delivery.messaging.Messenger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class DeliveryService {

    private final Messenger messenger;
    private final RiderRepository riders;
    private final DeliveryRepository deliveries;

    public DeliveryService(Messenger messenger, RiderRepository riders, DeliveryRepository deliveries) {
        this.riders = riders;
        this.deliveries = deliveries;
        this.messenger = messenger;
    }

    @Transactional
    public Delivery scheduleDelivery(DeliveryOrder deliveryOrder) {
        List<Rider> riders = this.riders.findAll();
        Optional<Rider> withLeastDeliveries = riders.stream().min(Comparator.comparingInt(Rider::getNrOfDeliveries));

        if (withLeastDeliveries.isPresent()) {
            Delivery newDelivery = new Delivery(deliveryOrder, withLeastDeliveries.get());
            deliveries.save(newDelivery);
            return newDelivery;
        } else {
            //Dit is natuurlijk fraaier met een custom exception type
            throw new RuntimeException("No available rider found");
        }
    }

    public int getMinutesRemaining(Delivery delivery) {
        String orderId = String.valueOf(delivery.getDeliveryOrder().getOrderId());
        Object response = messenger.sendAndReceive("orders-exchange", "orders.status", orderId);

        if (response instanceof byte[]) {
            String responseString = new String((byte[]) response);
            if (responseString.equals("\"Received\"")) {
                return (int) (Math.random() * 100);
            } else {
                return -1;
            }
        } else {
            System.out.println("Unexpected response type: " + response.getClass());
        }


        return -1;
    }
}
