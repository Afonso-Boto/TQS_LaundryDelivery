package tqs.project.laundryplatform.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tqs.project.laundryplatform.model.*;
import tqs.project.laundryplatform.repository.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks private OrderServiceImpl service;

    @Mock(lenient = true)
    private ComplaintRepository complaintRepository;

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @Mock(lenient = true)
    private OrderTypeRepository orderTypeRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private LaundryRepository laundryRepository;



    @BeforeEach
    void setUp() {
        Order order = new Order(1L, new Date(2022, 12, 12), 20.99);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(complaintRepository.save(Mockito.any(Complaint.class)))
                .thenReturn(new Complaint());
        Mockito.when(orderTypeRepository.findById(1L)).thenReturn(Optional.of(new OrderType("wash", 20.99)));
        Mockito.when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User("test", "test", "123", "test", 123)));
        Mockito.when(laundryRepository.findByName("default")).thenReturn(Optional.of(new Laundry("default", "default")));
    }

    @Test
    void testComplaint() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("orderId", 1L);
        json.put("title", "test");
        json.put("description", "test");

        assertThat(service.complaint(json)).isEqualTo(true);
    }

    @Test
    void testInvalidComplaint() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("orderId", 928373L);
        json.put("titlde", "test");
        json.put("desscription", "test");

        assertThat(service.complaint(json)).isEqualTo(false);
    }

    @Test
    void testCancelOrder() {
        assertThat(service.cancelOrder(1L)).isEqualTo(true);
    }

    @Test
    void testInvalidCancelOrder() {
        assertThat(service.cancelOrder(928373L)).isEqualTo(false);
    }
}
