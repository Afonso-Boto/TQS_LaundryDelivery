package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.hasCookie;
import static tqs.project.laundryplatform.controller.OrderController.ordersUncompleted;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tqs.project.laundryplatform.account.AccountManager;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.LoginResult;
import tqs.project.laundryplatform.account.RegisterRequest;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.qourier.Delivery;
import tqs.project.laundryplatform.qourier.QourierLoginRequest;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.UserRepository;
import tqs.project.laundryplatform.service.OrderService;

@Controller
@RequestMapping("/mobile")
public class MobileController {

    @Autowired
    AccountManager accountManager;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderService orderService;

    @PostMapping("/auth/register")
    public ResponseEntity<Boolean> signUpMobile(@RequestBody String requestBody, HttpServletResponse response) {
        JSONObject json = new JSONObject(requestBody);
        RegisterRequest request =
                new RegisterRequest(
                        json.getString("username"),
                        json.getString("email"),
                        json.getString("password"),
                        json.getString("fullName"),
                        json.getInt("phoneNumber"));

        if (!accountManager.register(request))
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Boolean> loginMobile(@RequestBody String requestBody, HttpServletResponse response) {
        JSONObject json = new JSONObject(requestBody);
        LoginRequest user = new LoginRequest(json.getString("username"), json.getString("password"));

        LoginResult result = accountManager.login(user);
        System.err.println("Login result: " + result);

        if (result.equals(LoginResult.WRONG_CREDENTIALS)
                || result.equals(LoginResult.NON_EXISTENT_ACCOUNT)) {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<String> logoutMobile(HttpServletResponse response) {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<String> ordersMobile(Model model, HttpServletRequest request, @RequestParam("username") String username) {
        System.err.println("orders");
        List<Order> orders;

        if (username.equals("admin"))
            orders = orderRepository.findAll();
        else
            orders = orderRepository.findAllByUser(userRepository.findByUsername(username).orElse(null));

        StringBuilder ordersString = new StringBuilder();
        for (Order order : orders) {
            System.err.println(order.toString());
            ordersString.append(order.toString());
        }
        System.err.println(ordersString.toString());

        ordersString = new StringBuilder(ordersString.substring(0, ordersString.length() - 1));

        System.err.println(ordersString);

        return new ResponseEntity<>(ordersString.toString(), HttpStatus.OK);
    }

    @GetMapping("/tracking")
    public ResponseEntity<String> trackingMobile(
            Model model, HttpServletRequest request, @RequestParam("orderId") String orderId) {
        return ResponseEntity.ok(
                Objects.requireNonNull(
                                orderRepository.findById(Long.parseLong(orderId)).orElse(null))
                        .toString());
    }

    @PostMapping("/order/cancelOrder/{id}")
    public ResponseEntity<String> cancelOrderMobile(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!hasCookie(request)) return ResponseEntity.status(401).body("unauthorized");

        orderService.cancelOrder(id);
        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping("/order/complaint")
    public ResponseEntity<Boolean> complaintMobile(@RequestBody String body, HttpServletRequest request, Model model) {
        System.out.println("complaint-mobile");
        System.out.println(body);
        JSONObject json = new JSONObject(body);


        if (orderService.complaint(json))
            return new ResponseEntity<>(true, HttpStatus.OK);


        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/order/init-order/{orderTypeId}/{cookieID}")
    public ResponseEntity<Boolean> initOrderMobile(
            Model model,
            HttpServletRequest request,
            @PathVariable("orderTypeId") long orderTypeId, @PathVariable("cookieID") String cookieID) {

        System.err.println(cookieID);
        long orderID = orderService.initOrder(orderTypeId, cookieID);

        if (orderID == -1) {
            System.err.println("SERA AQUI?????");
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }

        ordersUncompleted.put(cookieID, orderID);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/order/make-order/{cookieId}")
    public ResponseEntity<Boolean> newOrderMobile(@RequestBody String formObject, @PathVariable("cookieId") String cookieId, Model model, HttpServletRequest request)
            throws JsonProcessingException {
        System.out.println(formObject);
        formObject = formObject.substring(1, formObject.length() - 1);
        JSONObject orderInfo = new JSONObject(formObject);
        long orderId;


        orderId = ordersUncompleted.getOrDefault(cookieId, -1L);

        if (orderId == -1L) return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

        if (orderService.makeOrder(orderId, orderInfo)) {
            System.out.println("Order made");
            ordersUncompleted.remove(cookieId);

            // Qourier API Calls
            String uri = "http://51.142.110.251:80/api/v1/accounts/login";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            ObjectMapper objectMapper = new ObjectMapper();
            String json;
            HttpEntity<String> request1;

            json =
                    objectMapper.writeValueAsString(
                            new QourierLoginRequest("laundryathome@ua.pt", "123"));
            request1 = new HttpEntity<>(json, httpHeaders);
            String basicAuth = restTemplate.postForObject(uri, request1, String.class);

            uri = "http://51.142.110.251:80/api/v1/deliveries?basicAuth=" + basicAuth;
            Order order = orderRepository.findById(orderId).orElse(null);
            json =
                    objectMapper.writeValueAsString(
                            new Delivery(
                                    "laundryathome@ua.pt",
                                    40.631230465638644,
                                    -8.657472830474786,
                                    order.getDeliveryLocation() != null
                                            ? order.getDeliveryLocation()
                                            : "Universidade de Aveiro, 3810-193 Aveiro",
                                    "Universidade de Aveiro, 3810-193 Aveiro"));

            request1 = new HttpEntity<>(json, httpHeaders);
            String x = restTemplate.postForObject(uri, request1, String.class);


            System.out.println(x);

            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }


}
