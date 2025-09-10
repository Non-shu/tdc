package com.example.demo.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.repository.mybatis.EmpMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final EmpMapper empMapper;

    /**
     * 현재 로그인 사용자의 emp_id 반환.
     * - 로그인 주체 이름(username)을 loginKey로 간주 (사번 또는 로그인아이디)
     * - emp_no 또는 login_id 어느 쪽이든 매칭되면 emp_id 리턴
     */
    public long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginKey = (auth == null ? null : auth.getName());

        if (loginKey == null || loginKey.isBlank()) {
            throw new IllegalStateException("로그인 정보 없음");
        }

        Long empId = empMapper.findIdByLoginKey(loginKey);
        if (empId == null) {
            // 기존 메시지 형식 유지
            throw new IllegalStateException("사원 미존재: " + loginKey);
        }
        return empId;
    }

    /** 필요하면 사용 */
    public String loginKey() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null ? null : auth.getName());
    }
}
