package tqs.project.laundryplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.Item;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.model.OrderType;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.OrderTypeRepository;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderTypeRepository orderTypeRepository;

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
    public boolean makeOrder(long itemTypeId, List<Item> items) {
        return false;
    }

    @Override
    public boolean initOrder(long orderTypeId) {

        OrderType type = orderTypeRepository.findById(orderTypeId).orElse(null);
        if (type == null)
            return false;
        Order newOrder = new Order(type);
        orderRepository.save(newOrder);
        return true;
    }
}

