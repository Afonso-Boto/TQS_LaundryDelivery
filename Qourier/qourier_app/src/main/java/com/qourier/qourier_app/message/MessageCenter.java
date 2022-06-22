package com.qourier.qourier_app.message;

import static com.qourier.qourier_app.Utils.GSON;

import com.qourier.qourier_app.data.DeliveryState;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Data;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class MessageCenter {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageCenter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyRiderAssignment(String riderId, long deliveryId) {
        rabbitTemplate.send(
                RabbitConfiguration.TOPIC_EXCHANGE_NAME,
                generateRiderAssignmentTopic(riderId),
                MessageBuilder.withBody(String.valueOf(deliveryId).getBytes()).build());
    }

    @Data
    private static class DeliveryUpdate {
        private long deliveryId;
        private DeliveryState state;
    }

    public void sendDeliveryUpdate(long deliveryId, DeliveryState state) {
        DeliveryUpdate deliveryUpdate = new DeliveryUpdate();
        deliveryUpdate.setDeliveryId(deliveryId);
        deliveryUpdate.setState(state);

        rabbitTemplate.send(
                RabbitConfiguration.TOPIC_EXCHANGE_NAME,
                generateDeliveryUpdateTopic(deliveryId),
                MessageBuilder.withBody(GSON.toJson(deliveryUpdate).getBytes()).build());
    }

    public static String generateRiderAssignmentTopic(String riderId) {
        return RabbitConfiguration.RIDER_ASSIGNMENTS_ROUTING_KEY
                + "."
                + convertRiderIdToTopic(riderId);
    }

    public static String generateDeliveryUpdateTopic(long deliveryId) {
        return RabbitConfiguration.DELIVERY_UPDATES_ROUTING_KEY
                + "."
                + deliveryId;
    }

    private static String convertRiderIdToTopic(String riderId) {
        return Base64.getEncoder().encodeToString(riderId.getBytes(StandardCharsets.UTF_8));
    }
}
