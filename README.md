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

![아키텍처5png](https://github.com/user-attachments/assets/0ecd93ef-116f-41bc-976d-c221e9fca6a2)


### 🗂️ ERD DIAGRAM
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

## 기능 구현

### Google SMTP를 이용한 이메일 인증 회원가입 구현
- 회원가입 시 사용자의 이메일 주소로 인증 메일을 전송하고 사용자가 해당 이메일 내의 링크를 클릭하여 회원가입을 완료할 수 있도록 설계

### Kakaopay Open Api를 이용한 결제 서비스 구현
- 결제 요청부터 결제 승인, 결제 취소 등의 기능을 API를 통해 처리할 수 있도록 설계하여 간편 결제 기능을 제공

### Api Gateway를 이용하여 라우팅 및 인가 기능 구현
- 사용자 요청을 API Gateway가 받아서 마이크로서비스에 전달하고 인가 필터를 적용해 JWT 토큰을 검증하여 각 서비스에 적절한 요청만 전달

### Spring Eureka를 활용하여 마이크로 서비스 간의 통신 지원
- 각 마이크로 서비스가 Eureka에 등록되며 다른 서비스는 등록된 서비스의 위치를 동적으로 확인하여 통신할 수 있게 설계

### Spring Scheduler를 통해 주문 및 배송 관리
- 특정 시간이 되면 주문 상태를 자동으로 업데이트하고 배송 상태도 자동으로 변경되도록 구현


## 기술적 의사결정

### Redisson 분산락을 이용하여 결제 및 재고 관리를 원자적으로 처리

Redisson의 분산 락을 이용해 결제 처리 및 재고 관리에서 발생할 수 있는 동시성 문제를 해결했습니다. 
서버 확장을 고려하면서 분산 락을 적용하여 재고 데이터의 일관성을 유지하고 결제와 재고 차감 작업이 원자적으로 수행되도록 하여 데이터의 무결성을 보장했습니다.

### Redis Caching을 활용하여 효율적으로 재고 조회 및 업데이트 구현

Redis 캐싱을 사용하여 재고 조회 및 차감 작업을 처리함으로써 DB에 대한 부하를 줄이고 조회 성능을 크게 향상시켰습니다. 
또한 재고 차감 시 DB 업데이트는 RabbitMQ와 연계하여 성능을 최적화했습니다.

### RabbitMQ를 활용하여 이벤트를 비동기식으로 처리

RabbitMQ 메시지 큐를 사용하여 주문 생성, 결제 승인, 재고 차감 등의 중요한 이벤트를 비동기식으로 처리했습니다. 
이를 통해 서버의 처리 부담을 줄였고 동시에 장애가 발생해도 메시지는 큐 안에 남아있기 때문에 안전하게 복구할 수 있도록 하였습니다.

### Spring WebFlux, WebClient를 활용하여 결제 요청을 비동기식으로 처리

결제 API 요청 시 Spring WebFlux와 WebClient를 활용하여 논블로킹 비동기 방식으로 구현함으로써 대규모 트래픽을 처리할 수 있는 성능을 확보했습니다.
동기식 처리로 인한 서버 리소스의 과부하 문제를 해결하고 결제 과정에서의 응답 시간을 대폭 줄였습니다.

## 성능 개선 및 트러블 슈팅

### 1. 많은 사용자의 주문 및 결제 API에 대한 동기식 호출로 인한 서버 과부하 및 성능 저하

**[Before]**
10000개의 사용자 요청을 처리할 때 RabbitMQ를 이용해서 주문 생성과 카카오 결제 API 요청을 분리하여 비동기 이벤트 처리를 하려고 했으나 카카오 서버로 결제 api 호출하는 부분은 여전히 동기식 처리
RestTemplate을 사용하여 카카오 서버로 요청을 보내고 응답을 받는 것까지 동기적으로 처리되기 때문에 성능 저하가 발생하고 응답 시간이 길어짐

**[After]**
Spring WebFlux와 WebClient를 사용하여 카카오 결제 API 요청을 비동기식으로 전환
서버가 API 응답을 기다리지 않고 다른 요청을 처리할 수 있게 되어 병목 현상이 줄어들었고 전체 응답 시간이 최대 7분에서 1분 40초로 대폭 개선

### 2. 다수의 결제 승인 요청에서 발생한 재고 동시성 문제

**[Before]**
결제 승인 요청 시 동시성 제어 기능이 없어서 10000개의 요청 중 일부가 재고 업데이트에 실패하거나 누락

**[After]**
Redisson 분산락을 사용하여 동시성 제어를 효과적으로 구현
재고 업데이트 시 동시적으로 접근할 수 없도록 하여 트랜잭션 충돌을 방지하고 10000개의 요청에서 3000 ~ 4000개 누락이 발생하던 문제가 0개로 개선
서버 확장성을 고려해 분산 락을 적용하여 안전한 데이터 처리가 가능해짐

### 3. 재고 조회 및 차감 시 성능 및 일관성 문제

**[Before]**
재고 데이터를 DB에서 직접 조회 및 차감하는 방식으로 처리
다수의 요청이 동시에 DB에 접근하면서 부하가 증가하고 성능 저하와 병목 현상이 발생
또한 장애 발생 시 재고 데이터가 일관성 있게 처리되지 않아 데이터 손실 가능성이 존재

**[After]**
Redis 캐싱을 도입하여 재고 조회 시 빠른 응답을 제공하고 실시간 재고 업데이트는 RabbitMQ를 통해 처리하여 DB 부하를 줄임
재고 차감 시에만 DB를 업데이트하도록 설계하여 DB의 성능을 향상시켰고 장애 발생 시에도 RabbitMQ에 남아 있는 메시지로 인해 복구가 용이하게 됨

### 4. 주문 데이터 처리에서 데이터 일관성 문제

**[Before]**
주문 생성, 결제 정보 저장, 재고 업데이트 등이 각각의 서비스에서 독립적으로 처리되어 중간에 에러가 발생해도 일부 데이터만 DB에 저장되어 일관성 문제가 발생
분산 환경에서 데이터 일관성을 유지하는 별도의 조치가 없어 데이터가 불완전하게 저장되는 경우가 잦았음

**[After]**
SAGA 패턴을 도입하여 분산 트랜잭션 관리를 적용
각 단계가 성공적으로 완료되면 다음 단계로 넘어가고 실패 시에는 이전 단계의 작업을 취소(롤백)하여 데이터 일관성을 유지
이를 통해 모든 주문 처리 과정에서 원자성을 보장하게 됨

### 5. 사용자 Access Token에 대한 보안성 문제

**[Before]**
Access Token만 사용하여 사용자 인증을 처리
Access Token의 만료 기한을 길게 설정하면 보안성이 떨어지고 짧게 설정하면 사용자 편의성이 떨어짐
만료 기한이 길 경우 토큰 탈취 시 악용 가능성이 커짐

**[After]**
Access Token의 만료 기한을 짧게 설정하여 보안성을 강화하고 대신 Refresh Token을 발급하여 Access Token이 만료되면 재발급할 수 있도록 함
이를 통해 보안성과 사용자 편의성을 모두 개선





