package com.hanghae.paymentservice.domain.payment.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    // ConnectionProvider 설정 (기본값을 사용하면서 커스터마이징)
    ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
        .maxConnections(10000) // 최대 10000개의 연결 허용
        .pendingAcquireMaxCount(20000) // 대기열 크기 설정 (대기 중인 요청 최대 20000)
        .pendingAcquireTimeout(Duration.ofMillis(10000)) // 대기 타임아웃 10초
        .maxIdleTime(Duration.ofMinutes(5)) // 유휴 연결 유지 시간
        .evictInBackground(Duration.ofMinutes(5)) // 비활성 연결 제거 간격 설정
        .build();

    return builder
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 10초
                .responseTimeout(Duration.ofSeconds(15)) // 응답 타임아웃 15초
                .doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(15)) // 읽기 타임아웃 15초
                        .addHandlerLast(new WriteTimeoutHandler(15)) // 쓰기 타임아웃 15초
                )
        ))
        .build();
  }
}
