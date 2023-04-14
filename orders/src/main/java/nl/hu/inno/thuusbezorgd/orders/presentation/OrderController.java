package nl.hu.inno.thuusbezorgd.orders.presentation;

import nl.hu.inno.thuusbezorgd.orders.TimeProvider;
import nl.hu.inno.thuusbezorgd.orders.application.ReportService;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.*;
import nl.hu.inno.thuusbezorgd.orders.security.User;
import nl.hu.inno.thuusbezorgd.orders.security.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    public record AddressDto(String street, String nr, String city, String zip) {
        public AddressDto(Address a) {
            this(a.getStreet(), a.getHousenr(), a.getCity(), a.getZipcode());
        }

        public Address toAddress() {
            return new Address(city(), street(), nr(), zip());
        }
    }

    public record DishDto(Long id, String name) {

        public DishDto(long id) {
            this(id, null);
        }
    }

    public record OrderDto(AddressDto address, List<DishDto> dishes) {
    }

    public record OrderResponseDto(AddressDto address, List<DishDto> dishes, OrderStatus status) {
        public static OrderResponseDto fromOrder(Order o) {
            List<OrderedDish> orderedDishes = o.getOrderedDishes();

            // TODO: Message naar Menu service om DishDto's op te vragen op basis van de ID's
            // Tijdelijk
            List<DishDto> dtos = new ArrayList<DishDto>();
            dtos.add(new DishDto(7L, "Burger"));
            dtos.add(new DishDto(8L, "Vegaburger"));

//            List<DishDto> dtos = orderedDishes.stream().map(DishDto::new).collect(Collectors.toList());

            return new OrderResponseDto(new AddressDto(o.getAddress()), dtos, o.getStatus());
        }
    }

    private final OrderRepository orders;
    private final UserRepository users;
    private TimeProvider timeProvider;
    private ReportService reports;


    public OrderController(OrderRepository orders, UserRepository users, TimeProvider timeProvider, ReportService reports) {
        this.orders = orders;
        this.users = users;
        this.timeProvider = timeProvider;
        this.reports = reports;
    }

    @GetMapping()
    public List<OrderResponseDto> getOrders(User user) {
        return this.orders.findByUser(user).stream().map(OrderResponseDto::fromOrder).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponseDto> getOrder(User user, @PathVariable long id) {
        Optional<Order> order = this.orders.findById(id);
        if(order.isEmpty() || order.get().getUser() != user){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(OrderResponseDto.fromOrder(order.get()));
    }


    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Transactional
    public ResponseEntity<OrderResponseDto> placeOrder(User user, @RequestBody MultiValueMap<String, String> paramMap) throws URISyntaxException {
        List<DishDto> orderedDishes = new ArrayList<>();
        for (String d : paramMap.get("dish")) {
            long id = Long.parseLong(d);
            orderedDishes.add(new DishDto(id, ""));
        }

        String city = paramMap.getFirst("city");
        String street = paramMap.getFirst("street");
        String nr = paramMap.getFirst("nr");
        String zip = paramMap.getFirst("zip");

        return placeOrder(user, new OrderDto(new AddressDto(street, nr, city, zip), orderedDishes));
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public ResponseEntity<OrderResponseDto> placeOrder(User user, @RequestBody OrderDto newOrder) throws URISyntaxException {
        Order created = new Order(user, newOrder.address.toAddress());
        for (DishDto orderedDish : newOrder.dishes()) {
//            Optional<Dish> d = this.dishes.findById(orderedDish.id());
            // TODO: Message menu service to see if dish is present

            if (true /*d.isPresent()*/) {
                created.addDish(orderedDish.id);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Dish %s %s not found".formatted(orderedDish.id(), orderedDish.name()));
            }
        }

        Order savedOrder = this.orders.save(created);
        savedOrder.process(this.timeProvider.now());

        // TODO: Message Delivery service to initiate delivery

//        Delivery newDelivery = deliveries.scheduleDelivery(savedOrder);
//        savedOrder.setDelivery(newDelivery);

        return ResponseEntity
                .created(new URI("/orders/%d".formatted(savedOrder.getId())))
                .body(OrderResponseDto.fromOrder(savedOrder));

    }

    @GetMapping("report")
    public ResponseEntity<List<OrdersPerDayDTO>> getReport(){
        List<ReportService.OrdersPerDayDTO> orders = this.reports.generateOrderPerDayReport();

        return ResponseEntity.ok(orders.stream().map(o -> new OrdersPerDayDTO(o.year(), o.month(), o.day(), o.count())).toList());
    }

    public record OrdersPerDayDTO(int year, int month, int day, int orders) {
    }
}
