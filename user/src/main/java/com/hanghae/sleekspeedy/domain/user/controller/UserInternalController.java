package com.hanghae.sleekspeedy.domain.user.controller;

import com.hanghae.sleekspeedy.domain.user.service.UserInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

  private final UserInternalService userInternalService;


}
