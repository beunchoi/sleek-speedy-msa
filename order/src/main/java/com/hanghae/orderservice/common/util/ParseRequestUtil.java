package com.hanghae.orderservice.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class ParseRequestUtil {

    public String extractUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-Claim-sub");
        if (userIdHeader == null) {
            throw new RuntimeException("헤더에 사용자 ID가 없습니다.");
        }

        return userIdHeader;
    }

    public static void validateAdminRoleFromRequest(HttpServletRequest request) {
        String roleHeader = request.getHeader("X-Claim-auth");
        if (roleHeader == null) {
            throw new RuntimeException("헤더에 ROLE이 없습니다.");
        } else if (!roleHeader.equals("ADMIN")) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }
    }

}
