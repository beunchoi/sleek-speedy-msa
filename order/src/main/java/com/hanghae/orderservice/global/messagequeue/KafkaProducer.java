package com.hanghae.orderservice.global.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.orderservice.domain.order.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

//  public void send(String topic, OrderResponseDto orderDto) {
//    ObjectMapper mapper = new ObjectMapper();
//    String jsonInString = "";
//    try {
//      jsonInString = mapper.writeValueAsString(orderDto);
//    } catch (JsonProcessingException ex) {
//      ex.printStackTrace();
//    }
//
//    kafkaTemplate.send(topic, jsonInString);
//    log.info("kafka producer 가 데이터를 보냈습니다. " + orderDto);
//  }

  public void send(String topic, PaymentRequest request) {
    kafkaTemplate.executeInTransaction(k -> {
      ObjectMapper mapper = new ObjectMapper();
      String jsonInString = "";
      try {
        jsonInString = mapper.writeValueAsString(request);
      } catch (JsonProcessingException ex) {
        log.error("json 변환 실패: " + ex.getMessage());
        return false;
      }
      k.send(topic, jsonInString);
      return true;
    });
    log.info("kafka producer 가 데이터를 보냈습니다. " + request);
  }

//  public void sendSuccessMessage(Map<String, Object> responseMap) {
//    try {
//      ObjectMapper mapper = new ObjectMapper();
//      Map<String, Object> successPayload = new HashMap<>();
//      successPayload.put("response", responseMap);
//      String successMessage = mapper.writeValueAsString(successPayload);
//      kafkaTemplate.send("payment-success-topic", successMessage);
//      log.info("Payment success message sent to Kafka: {}", successMessage);
//    } catch (JsonProcessingException ex) {
//      log.error("Failed to send payment success message to Kafka", ex);
//    }
//  }

  public void sendFailureMessage(String originalMessage) {
    kafkaTemplate.send("payment-failure-topic", originalMessage);
    log.info("Payment failure message sent to Kafka: {}", originalMessage);
  }
}
