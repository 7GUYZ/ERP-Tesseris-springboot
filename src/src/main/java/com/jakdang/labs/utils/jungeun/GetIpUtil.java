package com.jakdang.labs.utils.jungeun;

import jakarta.servlet.http.HttpServletRequest;

public class GetIpUtil {
    /**
     * 클라이언트(사용자)의 실제 IP 주소를 반환하는 메서드
     * 프록시/로드밸런서 환경에서도 진짜 IP를 최대한 추출
     */
    public static String getClientIp(HttpServletRequest request){
        // 1. 프록시/로드밸런서가 있을 때, 원래 클라이언트 IP를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
        
        // 2. 위 헤더가 없거나 값이 비어있거나 unknown이면, 다른 헤더에서 시도
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // WebLogic, Apache 등에서 사용하는 헤더
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // WebLogic에서 사용하는 또 다른 헤더
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 3. 위의 모든 헤더가 없으면, 실제 요청을 보낸 네트워크의 IP(로컬 개발이면 127.0.0.1 또는 0:0:0:0:0:0:0:1)
        // 서버에 배포하지 않고 이용을 할 때에는(localhost 사용시) IP가 모두 0:0:0:0:0:0:0:1로 저장됩니다. (오류 아닙니다.)
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 4. X-Forwarded-For에 여러 IP가 있을 경우(프록시 여러 개 거칠 때), 첫 번째가 실제 클라이언트 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 5. 최종적으로 얻은 IP 반환
        return ip;
    }
}
