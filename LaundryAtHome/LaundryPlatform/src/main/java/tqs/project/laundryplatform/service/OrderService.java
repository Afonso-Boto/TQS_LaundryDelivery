package tqs.project.laundryplatform.service;

import java.util.List;
import org.json.JSONObject;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.OrderType;

public interface OrderService {
    List<Order> getOrder(int userID);

    List<OrderType> getOrderType();

    List<Item> getOrderItems(int orderID);

    List<Item> getItemTypes();

    boolean makeOrder(long orderID, JSONObject items);

    long initOrder(long orderTypeId, String cookieID);

    boolean complaint(JSONObject json);

    boolean cancelOrder(long orderId);
}
