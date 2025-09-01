package com.example.demo.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
	@GetMapping({ "/", "/home" })
	public String home(Model model, Authentication auth) {
		model.addAttribute("active", "dashboard");
		boolean isAdmin = auth != null && auth.getAuthorities().stream()
				.anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
		return isAdmin ? "views/admin" : "views/index";
	}
	
	@GetMapping("/approval")
	public String approval(Model model) {
		model.addAttribute("active", "approval");
		return "views/approval/list"; //결재관련 페이지 생성에정
	}
	
	@GetMapping("/login")
	public String login() {
		return "views/login";
	}	
}
