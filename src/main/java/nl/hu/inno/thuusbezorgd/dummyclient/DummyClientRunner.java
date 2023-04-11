package nl.hu.inno.thuusbezorgd.dummyclient;

import nl.hu.inno.thuusbezorgd.FakeTimeProvider;
import nl.hu.inno.thuusbezorgd.data.DishRepository;
import nl.hu.inno.thuusbezorgd.presentation.OrderController;
import nl.hu.inno.thuusbezorgd.security.BaseUser;
import nl.hu.inno.thuusbezorgd.security.UserRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DummyClientRunner {

    private final FakeTimeProvider timeProvider;
    private final UserRepository users;


    public DummyClientRunner(
            FakeTimeProvider timeProvider,
            UserRepository users) {
        this.timeProvider = timeProvider;
        this.users = users;
    }

    @Scheduled(fixedRate = 1000) //Zet dit laag om jezelf eens goed te DOSsen...
    public void generateOrders() throws Exception {
        List<BaseUser> allUsers = users.findAll().stream()
                .filter(p -> !p.getName().equals("admin")).toList();

        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate orderTemplate = builder.setReadTimeout(Duration.ofMillis(500)).build();


        List<Thread> threads = new ArrayList<>();
        for (BaseUser user : allUsers) {
            Thread orderThread = new Thread(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authentication-Hack", user.getName());

                OrderController.OrderDto order = new OrderController.OrderDto(
                        new OrderController.AddressDto("Somewhere" + user.getName(), "SomeNumber", "SomeCity", "SomeZip"),
                        List.of(
                                new OrderController.DishDto(7),
                                new OrderController.DishDto(8)
                        )
                );
                HttpEntity<OrderController.OrderDto> orderRequest = new HttpEntity<>(order, headers);


                ResponseEntity<OrderController.OrderResponseDto> resp =
                        orderTemplate.postForEntity(
                                "http://localhost:8080/orders", orderRequest, OrderController.OrderResponseDto.class);

                if (!resp.getStatusCode().is2xxSuccessful()) {

                    throw new RuntimeException("Dummy request borked! " + resp);
                }
                this.timeProvider.advanceMinute();

            });
            threads.add(orderThread);
            orderThread.start();
        }

        for(Thread t: threads){
            t.join();
        }

        this.timeProvider.advanceDay();
    }
}
