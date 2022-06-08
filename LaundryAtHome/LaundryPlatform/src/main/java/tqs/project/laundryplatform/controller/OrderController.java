package tqs.project.laundryplatform.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.project.laundryplatform.service.OrderService;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired OrderService orderService;
    private static final String REDIRECT_ORDER = "redirect:/new_order";

    private HashMap<String, Long> ordersUncompleted = new HashMap<>();

    @PostMapping("/make-order")
    public String makeOrder(@RequestBody String formObject, HttpServletRequest request) {
        JSONObject orderInfo = new JSONObject(formObject);
        long orderId;

        if (!hasCookie(request)) return "error";

        String cookieId = getIdFromCookie(request);
        orderId = ordersUncompleted.getOrDefault(cookieId, -1L);

        if (orderId == -1L) return "error";

        if (orderService.makeOrder(orderId, orderInfo)) {
            ordersUncompleted.remove(cookieId);
            return REDIRECT_ORDER;
        }

        return "error";
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

        return REDIRECT_ORDER;
    }
}
