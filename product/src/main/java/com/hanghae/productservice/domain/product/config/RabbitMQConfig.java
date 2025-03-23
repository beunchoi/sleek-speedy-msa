package com.hanghae.productservice.domain.product.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${spring.rabbitmq.host}")
  private String host;

  @Value("${spring.rabbitmq.username}")
  private String username;

  @Value("${spring.rabbitmq.password}")
  private String password;

  @Bean
  public CachingConnectionFactory rabbitConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
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
  public static final String DLX = "service.dlx";
  public static final String queueOrder = "service.order";
  public static final String queuePayment = "service.payment";
  public static final String queueProduct = "service.product";
  public static final String DLQ_PAYMENT = "service.dlq.payment";

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public TopicExchange deadLetterExchange() {
    return new TopicExchange(DLX);
  }

  @Bean
  public Queue queueOrder() {
    return new Queue(queueOrder);
  }
  @Bean
  public Queue queuePayment() {
    return QueueBuilder.durable(queuePayment)
        .withArgument("x-dead-letter-exchange", DLX)
        .withArgument("x-dead-letter-routing-key", DLQ_PAYMENT)
        .build();
  }
  @Bean
  public Queue queueProduct() {
    return new Queue(queueProduct);
  }
  @Bean
  public Queue dlqPayment() {
    return new Queue(DLQ_PAYMENT);
  }
  @Bean
  public Binding bindingOrder() {
    return BindingBuilder.bind(queueOrder()).to(exchange()).with(queueOrder);
  }
  @Bean
  public Binding bindingPayment() {
    return BindingBuilder.bind(queuePayment()).to(exchange()).with(queuePayment);
  }
  @Bean
  public Binding bindingProduct() {
    return BindingBuilder.bind(queueProduct()).to(exchange()).with(queueProduct);
  }
  @Bean
  public Binding bindingDlqPayment() {
    return BindingBuilder.bind(dlqPayment()).to(deadLetterExchange()).with(DLQ_PAYMENT);
  }

  public static final String exchangeErr = "service.err";
  public static final String queueErrOrder = "service.err.order";
  public static final String queueErrPayment = "service.err.payment";
  public static final String queueErrProduct = "service.err.product";

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
  public Queue queueErrProduct() {
    return new Queue(queueErrProduct);
  }

  @Bean
  public Binding bindingErrOrder() {
    return BindingBuilder.bind(queueErrOrder()).to(exchangeErr()).with(queueErrOrder);
  }
  @Bean
  public Binding bindingErrPayment() {
    return BindingBuilder.bind(queueErrPayment()).to(exchangeErr()).with(queueErrPayment);
  }
  @Bean
  public Binding bindingErrProduct() {
    return BindingBuilder.bind(queueErrProduct()).to(exchangeErr()).with(queueErrProduct);
  }

}
