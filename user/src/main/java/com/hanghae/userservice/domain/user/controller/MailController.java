package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.common.dto.ResponseMessage;
import com.hanghae.userservice.domain.user.dto.mail.MailAuthDto;
import com.hanghae.userservice.domain.user.dto.mail.MailRequestDto;
import com.hanghae.userservice.domain.user.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class MailController {

  private final MailService mailService;

  @PostMapping("/send")
  public ResponseEntity<ResponseMessage> mailSend(@RequestBody @Valid MailRequestDto mailRequestDto)
      throws MessagingException {
    String code = mailService.sendSimpleMessage(mailRequestDto.getEmail());

    ResponseMessage message = ResponseMessage.builder()
        .data(code)
        .statusCode(200)
        .resultMessage("인증번호 전송 완료")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @PostMapping("/auth")
  public ResponseEntity<?> authCheck(@RequestBody MailAuthDto mailAuthDto) {
    Boolean checked = mailService.checkAuthNum(mailAuthDto.getEmail(), mailAuthDto.getAuthNum());
    if (checked) {
      return ResponseEntity.status(HttpStatus.OK).body("이메일 인증 성공");
    }
    else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증 실패");
    }
  }

}
