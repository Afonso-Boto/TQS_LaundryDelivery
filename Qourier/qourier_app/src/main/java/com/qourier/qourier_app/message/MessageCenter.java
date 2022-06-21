package com.qourier.qourier_app.message;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    public static String generateRiderAssignmentTopic(String riderId) {
        return RabbitConfiguration.RIDER_ASSIGNMENTS_ROUTING_KEY
                + "."
                + convertRiderIdToTopic(riderId);
    }

    private static String convertRiderIdToTopic(String riderId) {
        return Base64.getEncoder().encodeToString(riderId.getBytes(StandardCharsets.UTF_8));
    }
}
