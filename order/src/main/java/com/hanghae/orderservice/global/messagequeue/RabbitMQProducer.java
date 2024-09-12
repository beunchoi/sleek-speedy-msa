package com.hanghae.orderservice.global.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQProducer {
  private final RabbitTemplate rabbitTemplate;

  public void sendToProduct(String queueName, OrderResponseDto orderDto) {
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = "";
    try {
      jsonInString = mapper.writeValueAsString(orderDto);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    }

    rabbitTemplate.convertAndSend(queueName, jsonInString);
    log.info("RabbitMQ producer가 데이터를 보냈습니다. " + orderDto);
  }
}
