package com.qourier.qourier_app.message;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.junit.RabbitAvailable;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * This requires a RabbitMQ instance to be running.
 * However, if one isn't running, then the test is ignored (@RabbitAvailable).
 */
@SpringJUnitConfig
@SpringRabbitTest
@DirtiesContext
@RabbitAvailable
class MessageCenterIntegrationTest {

    private static final String RIDER_ID = "rider@hotmail.com";
    private static final long DELIVERY_ID = 1L;
    private static final String RIDER_LISTENER_ID = "rider";

    @Autowired
    private MessageCenter messageCenter;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Test
    void whenNotifyRider_thenNotificationReceived() {
        messageCenter.notifyRiderAssignment(RIDER_ID, DELIVERY_ID);

        Listener listener = this.harness.getSpy(RIDER_LISTENER_ID);
        assertThat(listener).isNotNull();
        verify(listener).checkNotification(any());
    }

    @Configuration
    @EnableRabbit
    @RabbitListenerTest
    public static class Config {

        @Bean
        public Listener listener() {
            return new Listener();
        }

        @Bean
        @Primary
        public ConnectionFactory connectionFactory() {
            return new CachingConnectionFactory("localhost");
        }

        @Bean
        public Queue stompQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public TopicExchange topic() {
            return new TopicExchange("spring-boot-exchange");
        }

        @Bean
        public Binding binding() {
            return BindingBuilder.bind(stompQueue())
                    .to(topic())
                    .with(MessageCenter.generateRiderAssignmentTopic(RIDER_ID));
        }

        @Bean
        public RabbitAdmin admin(ConnectionFactory cf) {
            return new RabbitAdmin(cf);
        }

        @Bean
        public RabbitTemplate template(ConnectionFactory cf) {
            return new RabbitTemplate(cf);
        }

        @Bean
        public MessageCenter messageCenter(RabbitTemplate template) {
            return new MessageCenter(template);
        }

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
            SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
            containerFactory.setConnectionFactory(cf);
            return containerFactory;
        }
    }

    public static class Listener {

        @RabbitListener(id = RIDER_LISTENER_ID, queues = "#{stompQueue.name}")
        public void checkNotification(String notification) {
            assertThat(notification).isEqualTo(String.valueOf(DELIVERY_ID));
        }
    }

}
