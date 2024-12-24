### 📄 개요

**********************

<a name="readme-top"></a>
<div align="center">
<h1 align="center">sleek speedy</h1>
    <p align="center">
        한정 수량 상품 선착순 구매 서비스
    </p>
    <p align="center">
        2024.08 ~ 2024.09 (5주)
    </p>
</div>

## 프로젝트 소개
sleek speedy는 대규모 트래픽을 효율적으로 처리하여 한정 수량의 상품에 대한 선착순 구매를 지원하는 서비스입니다. 
사용자는 원하는 상품을 위시리스트에 담고 정해진 시간에 주문 및 결제를 진행하여 선착순으로 빠르게 결제를 완료하면 상품을 구매할 수 있습니다.

## 서비스 아키텍처
<br><br>
![아키텍처5png](https://github.com/user-attachments/assets/0ecd93ef-116f-41bc-976d-c221e9fca6a2)

<br><br>
### 🗂️ ERD DIAGRAM
<br><br>
![스크린샷 2024-10-02 123413](https://github.com/user-attachments/assets/a34e9504-d58c-4e22-a9e4-a0c1753776ce)


### 📜 API 명세서

## 개발환경

### Framework / Language
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/3.3.2-515151?style=for-the-badge">
<img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white"><img src="https://img.shields.io/badge/2023.0.3-515151?style=for-the-badge">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
<img src="https://img.shields.io/badge/java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"><img src="https://img.shields.io/badge/21-515151?style=for-the-badge">

### Libraries
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA"> <img src="https://img.shields.io/badge/WebClient-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="WebClient"> <img src="https://img.shields.io/badge/Redisson-FF0000?style=for-the-badge&logo=redis&logoColor=white" alt="Redisson"> <img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"> <img src="http://img.shields.io/badge/google smtp-4285F4?style=for-the-badge&logo=google&logoColor=white"> <img src="https://img.shields.io/badge/kakaopay OpenApi-ffcd00.svg?style=for-the-badge&logo=Kakao&logoColor=000000">


### Database
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">

### Messaging
<img src="https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white">

### DevOps
<img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">

### Test Tools
<img src="http://img.shields.io/badge/Jmeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white"> <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
<br><br>
## 기능 구현

### Google SMTP를 이용한 이메일 인증 회원가입 구현
- 회원가입 시 사용자의 이메일 주소로 인증 메일을 전송하고 사용자가 해당 이메일 내의 링크를 클릭하여 회원가입을 완료할 수 있도록 설계<br><br>

### Kakaopay Open Api를 이용한 결제 서비스 구현
- 결제 요청부터 결제 승인, 결제 취소 등의 기능을 API를 통해 처리할 수 있도록 설계하여 간편 결제 기능을 제공<br><br>

### Api Gateway를 이용하여 라우팅 및 인가 기능 구현
- 사용자 요청을 API Gateway가 받아서 마이크로서비스에 전달하고 인가 필터를 적용해 JWT 토큰을 검증하여 각 서비스에 적절한 요청만 전달<br><br>

### Spring Eureka를 활용하여 마이크로 서비스 간의 통신 지원
- 각 마이크로 서비스가 Eureka에 등록되며 다른 서비스는 등록된 서비스의 위치를 동적으로 확인하여 통신할 수 있게 설계<br><br>

### Spring Scheduler를 통해 주문 및 배송 관리
- 특정 시간이 되면 주문 상태를 자동으로 업데이트하고 배송 상태도 자동으로 변경되도록 구현<br><br>


## 기술적 의사결정

### Redisson 분산락을 이용하여 결제 및 재고 관리를 원자적으로 처리

Redisson의 분산 락을 이용해 결제 처리 및 재고 관리에서 발생할 수 있는 동시성 문제를 해결했습니다. 
서버 확장을 고려하면서 분산 락을 적용하여 재고 데이터의 일관성을 유지하고 결제와 재고 차감 작업이 원자적으로 수행되도록 하여 데이터의 무결성을 보장했습니다.<br><br>

### Redis Caching을 활용하여 효율적으로 재고 조회 및 업데이트 구현

Redis 캐싱을 사용하여 재고 조회 및 차감 작업을 처리함으로써 DB에 대한 부하를 줄이고 조회 성능을 크게 향상시켰습니다. 
또한 재고 차감 시 DB 업데이트는 RabbitMQ와 연계하여 성능을 최적화했습니다.<br><br>

### RabbitMQ를 활용하여 이벤트를 비동기식으로 처리

RabbitMQ 메시지 큐를 사용하여 주문 생성, 결제 승인, 재고 차감 등의 중요한 이벤트를 비동기식으로 처리했습니다. 
이를 통해 서버의 처리 부담을 줄였고 동시에 장애가 발생해도 메시지는 큐 안에 남아있기 때문에 안전하게 복구할 수 있도록 하였습니다.<br><br>

### Spring WebFlux, WebClient를 활용하여 결제 요청을 비동기식으로 처리

결제 API 요청 시 Spring WebFlux와 WebClient를 활용하여 논블로킹 비동기 방식으로 구현함으로써 대규모 트래픽을 처리할 수 있는 성능을 확보했습니다.
동기식 처리로 인한 서버 리소스의 과부하 문제를 해결하고 결제 과정에서의 응답 시간을 대폭 줄였습니다.<br><br>

## 성능 개선 및 트러블 슈팅

### 1. 다수의 카카오 결제 API 호출을 동기식으로 처리하여 성능 저하 발생     
**[자세히 보기](https://velog.io/@beunchoi/Spring-WebFlux-WebClient%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EA%B2%B0%EC%A0%9C-API-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94)**
    
**[Before]**

- RabbitMQ를 이용한 비동기 이벤트 처리로 주문 생성과 카카오 결제 API 요청을 분리
- Spring WebMVC와 RestTemplate을 활용하여 카카오 결제 API를 동기적으로 호출
- 10000개의 사용자 요청 시, RestTemplate이 카카오 결제 API를 호출하고 카카오 서버로부터 응답 받는 과정이 **동기식으로 처리**되어 성능 저하 발생

**[After]**

- **Spring WebFlux, WebClient를 활용**하여 카카오 결제 API를 **비동기식**으로 호출
- 응답 시간 : 최대 7분 → **1분 30초**로 대폭 개선
  
<br><br>

### 2. 재고 조회 및 차감 시 문제점 발생
[**자세히 보기**](https://velog.io/@beunchoi/%EC%9E%AC%EA%B3%A0-%EC%A1%B0%ED%9A%8C-%EB%B0%8F-%EC%B0%A8%EA%B0%90-%EC%B5%9C%EC%A0%81%ED%99%94-Redis%EC%99%80-RabbitMQ%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0)
    
**[Before]**

- 재고를 DB에서 직접 조회 및 차감하여 업데이트
- DB에 조회 요청이 많아지면 부하가 가중되고 동시에 업데이트가 발생하면 병목 현상이 생기거나 성능 저하 발생
- 재고 업데이트 과정에서 장애 발생 시 데이터 일관성 보장 어려움

**[After]**

- **Redis 캐싱을 이용**하여 재고 조회 및 업데이트 성능을 향상
    
    (응답 시간 : 평균 1545ms → **251ms**)
    
- **RabbitMQ 메시지 전송**을 통해 **재고 차감 시에만 DB를 업데이트**해서 DB 부하를 줄이고 확장성 높임
- 장애 발생 시에도 RabbitMQ에 처리되지 않은 메시지가 남아 있어 복구에 용이

<br><br>

 ### 3. 사용자 주문 시 여러 데이터의 업데이트 과정에서 일관성 유지에 실패 
 [**자세히 보기**](https://velog.io/@beunchoi/%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A3%BC%EB%AC%B8%EC%97%90%EC%84%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%9D%BC%EA%B4%80%EC%84%B1-%EC%9C%A0%EC%A7%80-%EC%8B%A4%ED%8C%A8%EC%99%80-%EA%B0%9C%EC%84%A0-SAGA-%ED%8C%A8%ED%84%B4-%EB%8F%84%EC%9E%85)
    
**[Before]**

- 분산 환경에서의 데이터 일관성 유지에 대한 별도의 조치가 없었음
- 사용자 주문 시 주문 생성 저장, 결제 정보 저장, 재고 업데이트 저장이 단계별로, 원자적으로 처리되지 않고 각각의 서비스에서 따로 진행
- 중간에 에러가 발생해도 앞서 DB에 저장된 데이터들은 남아 있어서 일관성 유지에 실패

**[After]**

- 분산 트랜잭션의 개념 중 하나인 **SAGA 패턴**을 적용
- 각 단계가 성공적으로 완료되면 다음 단계로 넘어가고 실패하면 이전 단계에서 수행된 작업을 취소하는 방식으로 데이터 일관성 유지에 성공

<br><br>

### 4. 다수의 결제 승인 요청에서 발생한 재고 동시성 문제
    
**[Before]**

- 동시성 제어 기능 없었음
- 10000개의 결제 승인 요청 시 재고 동시성 문제가 발생, 일부 재고는 DB 업데이트 누락 발생

**[After]**

- **Redisson 분산락을 이용**하여 동시성 문제 해결 (10000개 요청 시 3000 ~ 4000개 업데이트 누락 → 0개)
- 단일 서버의 경우 Synchronized 블록을 사용할 수 있지만 서버 확장성을 고려해 Redisson 분산락을 사용

<br><br>

### 5. 사용자 Access Token에 대한 보안성 문제
    
**[Before]**

- Access Token만 있었음
- Access Token의 만료 기한을 짧게 설정하면 사용자 편의성이 떨어짐
- Access Token의 만료 기한을 길게 설정하면 탈취 당할 경우 이를 악용할 수 있는 시간도 길어져 보안성 취약

**[After]**

- **Access Token의 만료 기한을 짧게 설정**해서 보안성 강화
- **Refresh Token의 만료 기한을 길게 설정**해서 Access Token이 만료될 때마다 재발급





