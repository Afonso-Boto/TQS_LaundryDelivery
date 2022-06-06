package tqs.project.laundryplatform.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/make-order")
    public String makeOrder(long itemTypeId, List<Item> items) {

        return "make order";
    }

    @PostMapping("/init-order")
    public String initOrder(Model model, HttpServletRequest request) {

        if (!hasCookie(request)) {
            return "error";
        }

        User user = (User) model.getAttribute("userId");




        return "init order";
    }
}
