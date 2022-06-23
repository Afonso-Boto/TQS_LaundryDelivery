package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.*;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.RegisterRequest;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.UserRepository;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*")
@Log4j2
public class MainController {

    private static final String REDIRECT_REGISTER = "redirect:/register";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_INDEX = "redirect:/index";
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String mainPage() {
        return REDIRECT_INDEX;
    }

    @GetMapping("/index")
    public String showIndex(Model model, HttpServletRequest request) {
        System.err.println("index");

        if (!hasCookie(request)) {
            System.err.println("cookie not verified");
            return REDIRECT_LOGIN;
        }

        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        System.err.println("get login");

        if (getIdFromCookie(request) != null) return REDIRECT_INDEX;

        model.addAttribute("loginRequest", new LoginRequest());
        return "login_form";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request) {
        System.err.println("register");

        model.addAttribute("registerRequest", new RegisterRequest());
        return "register_form";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        System.err.println("logout");

        if (hasCookie(request)) {
            removeCookie(response);
            return REDIRECT_LOGIN;
        } else {
            return "error";
        }
    }

    @GetMapping("/new_order")
    public String newOrder(Model model, HttpServletRequest request) {
        return "new_order";
    }

    @GetMapping("/orders")
    public ModelAndView orders(Model model, HttpServletRequest request) {
        System.err.println("orders");
        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject(
                "orders",
                orderRepository.findAllByUser(
                        userRepository.findByUsername(getIdFromCookie(request)).orElse(null)));
        return modelAndView;
    }

    @GetMapping("/orders-mobile")
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

    @GetMapping("/service")
    public String service(Model model, HttpServletRequest request) {
        return "service";
    }

    @GetMapping("/ok")
    public String ok(Model model, HttpServletRequest request) {
        System.out.println("ok");
        return "ok_page";
    }

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest request) {
        return "error";
    }

    @GetMapping("/tracking")
    public ModelAndView tracking(
            Model model, HttpServletRequest request, @RequestParam("orderId") String orderId) {

        Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);

        if (order == null || Objects.isNull(order.getDeliveryId())) {
            return new ModelAndView("error");
        }

        String uri = "http://51.142.110.251:80/api/v1/deliveries/progress/" + order.getDeliveryId();
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(uri, String.class);
        result = result.substring(1, result.length() - 1);

        order.setStatus(result);

        if (Objects.equals(Objects.requireNonNull(result), "DELIVERED")) {
            order.setCompleted(true);
            java.util.Date date = new java.util.Date();
            order.setDeliveryDate(new Date(date.getTime()));
        }

        orderRepository.save(order);

        ModelAndView mav = new ModelAndView("tracking");
        mav.addObject("order", order);

        return mav;
    }

    @GetMapping("/tracking-mobile")
    public ResponseEntity<String> trackingMobile(
            Model model, HttpServletRequest request, @RequestParam("orderId") String orderId) {
        return ResponseEntity.ok(
                Objects.requireNonNull(
                                orderRepository.findById(Long.parseLong(orderId)).orElse(null))
                        .toString());
    }
}
