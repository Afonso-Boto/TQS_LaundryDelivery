package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

//    @PostMapping("/make-order")
//    public String makeOrder(@ResponseBody ItemRequest json, long itemTypeId, List<Item> items) {
//        System.out.println();
//        return "make order";
//    }

    @RequestMapping(path = "/make-order", method = RequestMethod.POST)
    public String makeOrder(@RequestBody String formObject) {
        System.out.println(formObject);
        return "make order";
    }

    @PostMapping("/init-order")
    public String initOrder(long orderTypeId) {
        return "init order";
    }
}
