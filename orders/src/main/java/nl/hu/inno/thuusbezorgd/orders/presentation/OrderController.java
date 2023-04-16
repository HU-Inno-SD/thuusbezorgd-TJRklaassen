package nl.hu.inno.thuusbezorgd.orders.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.thuusbezorgd.orders.TimeProvider;
import nl.hu.inno.thuusbezorgd.orders.application.ReportService;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.messaging.Messenger;
import nl.hu.inno.thuusbezorgd.orders.security.User;
import nl.hu.inno.thuusbezorgd.orders.security.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Messenger messenger;
    private final ObjectMapper objectMapper;

    private final OrderRepository orders;
    private final UserRepository users;
    private TimeProvider timeProvider;
    private ReportService reports;

    public record AddressDto(String street, String nr, String city, String zip) {
        public AddressDto(Address a) {
            this(a.getStreet(), a.getHousenr(), a.getCity(), a.getZipcode());
        }

        public Address toAddress() {
            return new Address(city(), street(), nr(), zip());
        }
    }

    public record DishDTO(Long id, String name) {
        public DishDTO(long id) {
            this(id, null);
        }
    }

    public record OrderDTO(AddressDto address, List<DishDTO> dishes) {
    }

    public record OrderResponseDto(AddressDto address, List<DishDTO> dishes, OrderStatus status) {
        public static OrderResponseDto fromOrder(Order o, Messenger messenger) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<OrderedDish> orderedDishes = o.getOrderedDishes();
            List<DishDTO> dtos = new ArrayList<DishDTO>();

            for(OrderedDish od : orderedDishes) {
                String dishId = String.valueOf(od.getDishId());
                Object response = messenger.sendAndReceive("menu-exchange", "menu.dish.find", dishId);

                if (response instanceof byte[]) {
                    String responseString = new String((byte[]) response);

                    try {
                        DishDTO dishDTO = objectMapper.readValue(responseString, DishDTO.class);
                        dtos.add(dishDTO);
                    } catch (JsonMappingException e) {
                        System.err.println("Error mapping JSON to DishDTO: " + e.getMessage());
                    } catch (JsonProcessingException e) {
                        System.err.println("Error processing JSON: " + e.getMessage());
                    }
                } else {
                    System.out.println("Unexpected response type: " + response.getClass());
                }
            }

            return new OrderResponseDto(new AddressDto(o.getAddress()), dtos, o.getStatus());
        }
    }

    public OrderController(OrderRepository orders, UserRepository users, TimeProvider timeProvider,
                           ReportService reports, Messenger messenger) {
        this.orders = orders;
        this.users = users;
        this.timeProvider = timeProvider;
        this.reports = reports;
        this.messenger = messenger;
        objectMapper = new ObjectMapper();
    }

    @GetMapping()
    public List<OrderResponseDto> getOrders(User user) {
        return this.orders.findByUser(user).stream()
            .map(o -> OrderResponseDto.fromOrder(o, messenger))
            .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponseDto> getOrder(User user, @PathVariable long id) {
        Optional<Order> order = this.orders.findById(id);
        if(order.isEmpty() || order.get().getUser() != user){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderResponseDto.fromOrder(order.get(), messenger));
    }


    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Transactional
    public ResponseEntity<OrderResponseDto> placeOrder(User user, @RequestBody MultiValueMap<String, String> paramMap) throws URISyntaxException {
        List<DishDTO> orderedDishes = new ArrayList<>();
        for (String d : paramMap.get("dish")) {
            long id = Long.parseLong(d);
            orderedDishes.add(new DishDTO(id, ""));
        }

        String city = paramMap.getFirst("city");
        String street = paramMap.getFirst("street");
        String nr = paramMap.getFirst("nr");
        String zip = paramMap.getFirst("zip");

        return placeOrder(user, new OrderDTO(new AddressDto(street, nr, city, zip), orderedDishes));
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public ResponseEntity<OrderResponseDto> placeOrder(User user, @RequestBody OrderDTO newOrder) throws URISyntaxException {
        Order created = new Order(user, newOrder.address.toAddress());
        for (DishDTO orderedDish : newOrder.dishes()) {
            String dishId = String.valueOf(orderedDish.id());
            Object response = messenger.sendAndReceive("menu-exchange", "menu.dish.available", dishId);

            boolean available = false;

            if (response instanceof byte[]) {
                available = Boolean.parseBoolean(new String((byte[]) response));
            } else {
                System.out.println("Unexpected response type: " + response.getClass());
            }

            if (available) {
                created.addDish(orderedDish.id);
                messenger.send("menu-exchange", "menu.dish.prepare", dishId);

                Order savedOrder = this.orders.save(created);
                savedOrder.process(this.timeProvider.now());

                messenger.send("delivery-exchange", "delivery.start", String.valueOf(created.getId()));

                return ResponseEntity
                        .created(new URI("/orders/%d".formatted(savedOrder.getId())))
                        .body(OrderResponseDto.fromOrder(savedOrder, messenger));
            } else {
                System.out.println("Dish not available.");
            }
        }



        // TODO: Message Delivery service to initiate delivery

//        Delivery newDelivery = deliveries.scheduleDelivery(savedOrder);
//        savedOrder.setDelivery(newDelivery);

        return null;
    }

    @GetMapping("report")
    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrderPerDayReport();

        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
    }

    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
    }
}
