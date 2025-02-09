package com.hanghae.paymentservice.domain.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Bean
  public CachingConnectionFactory rabbitConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    return connectionFactory;
  }

  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    return rabbitTemplate;
  }

  public static final String exchange = "service";
  public static final String queuePayment = "service.payment";
  public static final String queueProduct = "service.product";

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Queue queuePayment() {
    return new Queue(queuePayment);
  }
  @Bean
  public Queue queueProduct() {
    return new Queue(queueProduct);
  }
  @Bean
  public Binding bindingPayment() {
    return BindingBuilder.bind(queuePayment()).to(exchange()).with(queuePayment);
  }
  @Bean
  public Binding bindingProduct() {
    return BindingBuilder.bind(queueProduct()).to(exchange()).with(queueProduct);
  }

  public static final String exchangeErr = "service.err";
  public static final String queueErrOrder = "service.err.order";
  public static final String queueErrPayment = "service.err.payment";

  @Bean
  public TopicExchange exchangeErr() {
    return new TopicExchange(exchangeErr);
  }

  @Bean
  public Queue queueErrOrder() {
    return new Queue(queueErrOrder);
  }
  @Bean
  public Queue queueErrPayment() {
    return new Queue(queueErrPayment);
  }

  @Bean
  public Binding bindingErrOrder() {
    return BindingBuilder.bind(queueErrOrder()).to(exchangeErr()).with(queueErrOrder);
  }
  @Bean
  public Binding bindingErrPayment() {
    return BindingBuilder.bind(queueErrPayment()).to(exchangeErr()).with(queueErrPayment);
  }

}
