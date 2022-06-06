package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tqs.project.laundryplatform.model.Item;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/make-order")
    public String makeOrder(long itemTypeId, List<Item> items) {

        return "make order";
    }

    @PostMapping("/init-order")
    public String initOrder(long orderTypeId) {
        return "init order";
    }
}
