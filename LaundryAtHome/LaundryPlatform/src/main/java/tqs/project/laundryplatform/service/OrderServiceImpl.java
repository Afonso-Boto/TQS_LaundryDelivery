package tqs.project.laundryplatform.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.*;
import tqs.project.laundryplatform.repository.LaundryRepository;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.OrderTypeRepository;
import tqs.project.laundryplatform.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired OrderRepository orderRepository;
    @Autowired UserRepository userRepository;
    @Autowired OrderTypeRepository orderTypeRepository;
    @Autowired LaundryRepository laundryRepository;

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
    public long initOrder(long orderTypeId, String cookieID) {

        OrderType type = orderTypeRepository.findById(orderTypeId).orElse(null);
        User user = userRepository.findByUsername(cookieID).orElse(null);
        Laundry laundry = laundryRepository.findByName("default").orElse(null);

        if (type == null || user == null || laundry == null) return -1;

        Order newOrder = new Order(type, user, laundry);

        orderRepository.save(newOrder);
        return newOrder.getId();
    }
}
