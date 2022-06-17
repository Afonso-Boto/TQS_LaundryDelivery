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
import tqs.project.laundryplatform.model.Complaint;
import tqs.project.laundryplatform.model.Order;
import tqs.project.laundryplatform.repository.ComplaintRepository;
import tqs.project.laundryplatform.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks private OrderServiceImpl service;

    @Mock(lenient = true)
    private ComplaintRepository complaintRepository;

    @Mock(lenient = true)
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        Order order = new Order(1L, new Date(2022, 12, 12), 20.99);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(complaintRepository.save(Mockito.any(Complaint.class)))
                .thenReturn(new Complaint());
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
