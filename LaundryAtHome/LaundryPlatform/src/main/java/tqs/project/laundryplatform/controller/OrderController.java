package tqs.project.laundryplatform.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.ItemType;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.ItemRepository;
import tqs.project.laundryplatform.repository.ItemTypeRepository;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.UserRepository;
import tqs.project.laundryplatform.service.OrderService;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;
import static tqs.project.laundryplatform.controller.AuthController.hasCookie;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final String REDIRECT_ORDER = "redirect:/new_order";

    private HashMap<String, Long> ordersUncompleted = new HashMap<>();

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired ItemTypeRepository itemTypeRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired UserRepository userRepository;

    @PostMapping("/make-order")
    public String makeOrder(@RequestBody String formObject, HttpServletRequest request) {
        JSONObject orderInfo = new JSONObject(formObject);
        long orderId;
        Order newOrder;
        System.err.println(orderInfo);

        if (!hasCookie(request)) return "error";

        String cookieId = getIdFromCookie(request);
        orderId = ordersUncompleted.getOrDefault(cookieId, -1L);

        if (orderId == -1L) return "error";

        newOrder = orderRepository.findById(orderId).orElse(null);
        if (newOrder == null) return "null";

        Set<Item> items = new HashSet<>();

        if (orderInfo.get("its") == JSONObject.NULL) return "error";

        JSONArray itemsArray = orderInfo.getJSONArray("its");
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObject = itemsArray.getJSONObject(i);
            Item item;

            boolean isDark;
            int number;
            ItemType itemType;

            if (itemObject.get("itemType") == JSONObject.NULL) return "error";
            String itemTypeId = itemObject.getString("itemType").toLowerCase();
            System.err.println(itemTypeId);
            itemType = itemTypeRepository.findByName(itemTypeId).orElse(null);
            System.err.println("itemType: " + itemType.getName());
            if (itemType == null) return "error";
            if (itemObject.get("isDark") == JSONObject.NULL) return "error";
            if (itemObject.getString("isDark").equals("Claras")) {
                isDark = false;
            } else if (itemObject.getString("isDark").equals("Escuras")) {
                isDark = true;
            } else {
                return "error";
            }

            if (itemObject.get("number") == JSONObject.NULL) return "error";

            number = Integer.valueOf(itemObject.getString("number"));


            item = new Item(number, isDark, newOrder, itemType);
            System.err.println("item: " + item.getItemType().getName() + " " + item.getNumber());
            items.add(item);
            itemRepository.save(item);
        }

        newOrder.setItems(items);

        return "ok";
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
