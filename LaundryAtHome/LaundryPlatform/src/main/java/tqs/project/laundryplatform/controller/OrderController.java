package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {


    @PostMapping("/make-order")
    public String makeOrder(@RequestBody String formObject) {
        System.out.println(formObject);
        return "make order";
    }

    @PostMapping("/init-order")
    public String initOrder(long orderTypeId) {
        return "init order";
    }
}
