package com.qourier.qourier_app.message;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageCenter {

    private final RabbitTemplate rabbitTemplate;

    public MessageCenter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyRiderAssignment(String riderId, long deliveryId) {
        rabbitTemplate.convertAndSend(generateRiderAssignmentTopic(riderId), deliveryId);
    }

    public String generateRiderAssignmentTopic(String riderId) {
        return RabbitConfiguration.RIDER_ASSIGNMENTS_ROUTING_KEY + "." + convertRiderIdToTopic(riderId);
    }

    private String convertRiderIdToTopic(String riderId) {
        return DigestUtils.sha256Hex(riderId);
    }
}
