package com.example.demo.support;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.example.demo.repository.mybatis.EmpMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequestScope
@RequiredArgsConstructor
public class CurrentUser {
	private final EmpMapper mapper;
	
	public String loginId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (auth == null ? null : auth.getName());
	}

	public Long id() {
		var ctx = SecurityContextHolder.getContext();
		var auth = (ctx != null) ? ctx.getAuthentication() : null;
		if (auth == null || !auth.isAuthenticated()) {
			throw new AccessDeniedException("로그인 필요");
		}
		String empNo = auth.getName(); // 로그인 아이디 = 사번(직원코드)라고 가정
		Long empId = mapper.findIdByEmpNo(empNo);
		if (empId == null)
			throw new IllegalStateException("사원 미존재: " + empNo);
		return empId;
	}
	
	public String name() {
		Long id = id();
		return (id == null ? null : mapper.findEmpNameById(id));
	}
}
