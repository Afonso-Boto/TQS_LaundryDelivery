package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.qourier.Delivery;
import tqs.project.laundryplatform.qourier.QourierLoginRequest;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.service.OrderService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/order")
public class OrderController {

    @Autowired OrderService orderService;
    @Autowired MainController mainController;
    @Autowired OrderRepository orderRepository;

    private static final String REDIRECT_NEW_ORDER = "redirect:/new_order";
    private static final String REDIRECT_ORDERS = "redirect:/orders";
    private static final String REDIRECT_ERROR = "redirect:/error";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    private HashMap<String, Long> ordersUncompleted = new HashMap<>();

    @PostMapping("/cancelOrder/{id}")
    public String cancelOrder(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!hasCookie(request)) return REDIRECT_LOGIN;

        orderService.cancelOrder(id);
        return REDIRECT_ORDERS;
    }

    @PostMapping("cancelOrder-mobilde/{id}")
    public ResponseEntity<String> cancelOrderMobile(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!hasCookie(request)) return ResponseEntity.status(401).body("unauthorized");

        orderService.cancelOrder(id);
        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping("/complaint")
    public String complaint(@RequestBody String body, HttpServletRequest request, Model model) {
        JSONObject json = new JSONObject(body);

        if (hasCookie(request)) {
            orderService.complaint(json);
            return REDIRECT_ORDERS;
        }

        return REDIRECT_ERROR;
    }

    @PostMapping("/complaint-mobile")
    public ResponseEntity<String> complaintMobile(@RequestBody String body, HttpServletRequest request, Model model) {
        JSONObject json = new JSONObject(body);

        if (hasCookie(request)) {
            orderService.complaint(json);
            return ResponseEntity.ok("OK");
        }

        return ResponseEntity.status(401).body("Unauthorized");
    }

    @PostMapping("/make-order")
    public String makeOrder(@RequestBody String formObject, Model model, HttpServletRequest request)
            throws JsonProcessingException {
        JSONObject orderInfo = new JSONObject(formObject);
        long orderId;

        if (!hasCookie(request)) return "error";

        String cookieId = getIdFromCookie(request);
        orderId = ordersUncompleted.getOrDefault(cookieId, -1L);

        if (orderId == -1L) return "error";

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

            return "redirect:/ok";
        }

        return "redirect:/error";
    }

    @PostMapping("/make-order-mobile/{cookieId}")
    public ResponseEntity<Boolean> newOrderMobile(@RequestBody String formObject,@PathVariable("cookieId") String cookieId, Model model, HttpServletRequest request)
            throws JsonProcessingException {
        System.out.println(formObject);
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

    @GetMapping("/init-order")
    public String initOrder(
            Model model,
            HttpServletRequest request,
            @RequestParam("orderTypeId") long orderTypeId) {

        if (!hasCookie(request)) {
            return "error";
        }

        String cookieID = getIdFromCookie(request);

        long orderID = orderService.initOrder(orderTypeId, cookieID);

        if (orderID == -1) {
            return "error";
        }

        ordersUncompleted.put(cookieID, orderID);

        return REDIRECT_NEW_ORDER;
    }

    @GetMapping("/init-order-mobile/{orderTypeId}/{cookieID}")
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
}
