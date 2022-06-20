package com.qourier.qourier_app.message;

//@Configuration
//@EnableRabbit
public class RabbitConfiguration {

    public static final String TOPIC_EXCHANGE_NAME = "spring-boot-exchange";
    public static final String QUEUE_NAME = "spring-boot";

    public static final String RIDER_ASSIGNMENTS_ROUTING_KEY = "rider.assignments";

//    @Bean
//    public Queue queue() {
//        return new Queue(QUEUE_NAME, true);
//    }
//
//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(TOPIC_EXCHANGE_NAME);
//    }
//
//    @Bean
//    public Binding bindingRiderAssignments(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(RIDER_ASSIGNMENTS_ROUTING_KEY + ".#");
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        return new RabbitTemplate(connectionFactory);
//    }
}
