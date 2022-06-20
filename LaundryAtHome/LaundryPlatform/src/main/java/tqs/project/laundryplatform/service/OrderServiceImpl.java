package tqs.project.laundryplatform.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.*;
import tqs.project.laundryplatform.repository.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired LaundryRepository laundryRepository;
    @Autowired OrderTypeRepository orderTypeRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired ItemTypeRepository itemTypeRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired UserRepository userRepository;
    @Autowired ComplaintRepository complaintRepository;

    @Override
    public List<Order> getOrder(int userID) {
        return null;
    }

    @Override
    public List<OrderType> getOrderType() {
        return null;
    }

    @Override
    public List<Item> getOrderItems(int orderID) {
        return null;
    }

    @Override
    public List<Item> getItemTypes() {
        return null;
    }

    @Override
    public boolean makeOrder(long orderId, JSONObject orderInfo) {
        Order newOrder;
        String address = "";

        newOrder = orderRepository.findById(orderId).orElse(null);
        if (newOrder == null) return false;

        Set<Item> items = new HashSet<>();

        if (orderInfo.get("its") == JSONObject.NULL) return false;

        JSONArray itemsArray = orderInfo.getJSONArray("its");

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObject = itemsArray.getJSONObject(i);
            Item item;

            boolean isDark;
            int number;
            ItemType itemType;

            if (itemObject.get("itemType") == JSONObject.NULL) return false;
            String itemTypeId = itemObject.getString("itemType").toLowerCase();

            itemType = itemTypeRepository.findByName(itemTypeId).orElse(null);

            if (itemType == null) return false;
            if (itemObject.get("isDark") == JSONObject.NULL) return false;

            if (itemObject.getString("isDark").equals("Claras")) {
                isDark = false;
            } else if (itemObject.getString("isDark").equals("Escuras")) {
                isDark = true;
            } else {
                return false;
            }

            if (itemObject.get("number") == JSONObject.NULL) return false;

            number = Integer.valueOf(itemObject.getString("number"));

            item = new Item(number, isDark, newOrder, itemType);
            items.add(item);
            itemRepository.save(item);

            if (itemObject.get("address") != JSONObject.NULL) {
                address = itemObject.getString("address");
            }
        }
        System.out.println("address: " + address);

        newOrder.setItems(items);
        newOrder.setDeliveryLocation(address);
        orderRepository.save(newOrder);

        return true;
    }

    @Override
    public long initOrder(long orderTypeId, String cookieID) {

        OrderType type = orderTypeRepository.findById(orderTypeId).orElse(null);
        User user = userRepository.findByUsername(cookieID).orElse(null);
        Laundry laundry = laundryRepository.findByName("default").orElse(null);

        if (type == null || user == null || laundry == null) return -1;

        Order newOrder = new Order(type, user, laundry);

        orderRepository.save(newOrder);
        return newOrder.getId();
    }

    @Override
    public boolean complaint(JSONObject json) {
        long orderId;
        String title, description;

        try {
            orderId = Long.parseLong(json.getString("orderId"));
            title = json.getString("title");
            description = json.getString("description");
        } catch (JSONException e) {
            return false;
        }

        if (orderId == -1 || title == null || description == null) return false;

        Complaint complaint =
                new Complaint(title, description, orderRepository.findById(orderId).orElse(null));
        complaintRepository.save(complaint);

        return true;
    }

    @Override
    public boolean cancelOrder(long orderId) {
        if (orderId == -1) return false;

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;

        orderRepository.delete(order);
        return true;
    }
}
