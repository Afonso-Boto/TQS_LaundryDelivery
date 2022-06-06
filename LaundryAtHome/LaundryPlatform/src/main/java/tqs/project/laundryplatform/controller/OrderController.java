package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.OrderType;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;
import tqs.project.laundryplatform.service.OrderService;

import javax.persistence.FieldResult;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

@Controller
@RequestMapping("/order")
public class OrderController {
    private static final String REDIRECT_ORDER = "redirect:/new_order";

    private HashMap<Long, String> ordersUncompleted = new HashMap<>();

    @Autowired OrderService orderService;
    @Autowired UserRepository userRepository;



    @PostMapping("/make-order")
    public String makeOrder(@RequestBody String formObject) {
        System.out.println(formObject);
        return "make order";
    }

    @GetMapping("/init-order")
    public String initOrder(Model model, HttpServletRequest request, @RequestParam("orderTypeId") long orderTypeId) {

        if (!hasCookie(request)) {
            return "error";
        }

        String cookieID = getIdFromCookie(request);

        long orderID = orderService.initOrder(orderTypeId, cookieID);

        if (orderID == -1) {
            return "error";
        }

        ordersUncompleted.put(orderID, cookieID);

        return REDIRECT_ORDER;
    }
}
