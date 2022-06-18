package com.qourier.qourier_app.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class MessageCenter {

    private final RabbitTemplate rabbitTemplate;

    public MessageCenter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyRiderAssignment(String riderId, long deliveryId) {
        rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE_NAME, generateRiderAssignmentTopic(riderId), deliveryId);
    }

    public String generateRiderAssignmentTopic(String riderId) {
        return RabbitConfiguration.RIDER_ASSIGNMENTS_ROUTING_KEY + "." + convertRiderIdToTopic(riderId);
    }

    private String convertRiderIdToTopic(String riderId) {
        return Base64.getEncoder().encodeToString(riderId.getBytes(StandardCharsets.UTF_8));
    }
}
