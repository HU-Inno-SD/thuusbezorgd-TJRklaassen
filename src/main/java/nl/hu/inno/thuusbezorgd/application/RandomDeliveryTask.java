package nl.hu.inno.thuusbezorgd.application;

import nl.hu.inno.thuusbezorgd.data.DeliveryRepository;
import nl.hu.inno.thuusbezorgd.domain.Delivery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class RandomDeliveryTask {
    private final DeliveryRepository deliveries;

    public RandomDeliveryTask(DeliveryRepository deliveries) {
        this.deliveries = deliveries;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void pretendActualRealWorldStuff() {
        Optional<Delivery> delivery = this.deliveries.findRandomDelivery();

        if (delivery.isPresent()) {
            Delivery someDelivery = delivery.get();
            someDelivery.getOrder().setStatus(someDelivery.getOrder().getStatus().next());
        }
    }
}
