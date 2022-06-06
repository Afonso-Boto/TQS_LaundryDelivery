package tqs.project.laundryplatform.service;

import java.util.List;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.OrderType;

public interface OrderService {
    List<Order> getOrder(int userID);

    List<OrderType> getOrderType();

    List<Item> getOrderItems(int orderID);

    List<Item> getItemTypes();

    boolean makeOrder(long itemTypeId, List<Item> items);

    boolean initOrder(long orderTypeId);
}
