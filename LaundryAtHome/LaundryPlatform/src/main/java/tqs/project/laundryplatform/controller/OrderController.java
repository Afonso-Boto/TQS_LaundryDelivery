package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.qourier.Delivery;
import tqs.project.laundryplatform.qourier.DeliveryCreation;
import tqs.project.laundryplatform.qourier.QourierLoginRequest;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.service.OrderService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    MainController mainController;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RabbitAdmin rabbitAdmin;

    private static final String REDIRECT_NEW_ORDER = "redirect:/new_order";
    private static final String REDIRECT_ORDERS = "redirect:/orders";
    private static final String REDIRECT_ERROR = "redirect:/error";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    static HashMap<String, Long> ordersUncompleted = new HashMap<>();

    @PostMapping("/cancelOrder/{id}")
    public String cancelOrder(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!hasCookie(request)) return REDIRECT_LOGIN;

        orderService.cancelOrder(id);
        return REDIRECT_ORDERS;
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
            String response = restTemplate.postForObject(uri, request1, String.class);
            System.out.println(response);

            Gson gson = new GsonBuilder().create();
            DeliveryCreation deliveryCreation = gson.fromJson(response, DeliveryCreation.class);

            Order orderToUpdate = orderRepository.findById(orderId).orElse(null);
            if (orderToUpdate != null) {
                orderToUpdate.setDeliveryId(deliveryCreation.getDeliveryId());
                orderToUpdate.setStatus(deliveryCreation.getDeliveryState());
                orderRepository.save(orderToUpdate);
            }

            System.out.println(deliveryCreation);

            // RabbitMQ Logic
//            Queue queue = new Queue("", false, true, true);
//            TopicExchange topicExchange = new TopicExchange("spring-boot-exchange");
//            String queueName = rabbitAdmin.declareQueue(queue);
//            Binding subscription = BindingBuilder.bind(queue).to(topicExchange).with("delivery.updates." + deliveryCreation.getDeliveryId());
//            rabbitAdmin.declareBinding(subscription);
//            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//            container.setConnectionFactory(rabbitAdmin.getRabbitTemplate().getConnectionFactory());
//            container.setQueueNames(queueName);
//            container.setMessageListener(message -> {
//                DeliveryUpdate update = gson.fromJson(Arrays.toString(message.getBody()), DeliveryUpdate.class);
//                System.out.println(update);
//
//                long orderIdForUpdate = orderRepository.findByDeliveryId(update.getDeliveryId()).getId();
//                orderService.updateOrder(orderIdForUpdate, update);
//            });
//            container.start();

            return "redirect:/ok";
        }

        return "redirect:/error";
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
}
