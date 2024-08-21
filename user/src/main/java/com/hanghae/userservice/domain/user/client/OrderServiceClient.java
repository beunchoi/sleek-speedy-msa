package com.hanghae.userservice.domain.user.client;

import com.hanghae.userservice.domain.user.dto.OrderResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
  @GetMapping("/order-service/{userId}/orders")
  List<OrderResponseDto> getOrdersByUserId(@PathVariable String userId);
}
