package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.project.laundryplatform.service.OrderService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/order")
public class OrderController {

    @Autowired OrderService orderService;
    @Autowired MainController mainController;

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

    @PostMapping("/complaint")
    public String complaint(@RequestBody String body, HttpServletRequest request, Model model) {
        System.out.println("------>complaint");
        JSONObject json = new JSONObject(body);

        if (hasCookie(request)){
            orderService.complaint(json);
            return REDIRECT_ORDERS;
        }

        return REDIRECT_ERROR;
    }

    @PostMapping("/make-order")
    public String makeOrder(
            @RequestBody String formObject, Model model, HttpServletRequest request) {
        JSONObject orderInfo = new JSONObject(formObject);
        long orderId;

        if (!hasCookie(request)) return "error";

        String cookieId = getIdFromCookie(request);
        orderId = ordersUncompleted.getOrDefault(cookieId, -1L);

        if (orderId == -1L) return "error";

        if (orderService.makeOrder(orderId, orderInfo)) {
            System.out.println("Order made");
            ordersUncompleted.remove(cookieId);
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
