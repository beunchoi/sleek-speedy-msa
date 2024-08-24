package com.hanghae.orderservice.global.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void send(String topic, OrderResponseDto orderDto) {
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = "";
    try {
      jsonInString = mapper.writeValueAsString(orderDto);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    }

    kafkaTemplate.send(topic, jsonInString);
    log.info("kafka producer 가 데이터를 보냈습니다. " + orderDto);
  }
}
