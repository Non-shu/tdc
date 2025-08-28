package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		model.addAttribute("active", "dashboard");
		return "views/home";
	}
	
	@GetMapping("/approval")
	public String approval(Model model) {
		model.addAttribute("active", "approval");
		return "views/approval/list"; //결재관련 페이지 생성에정
	}
	
}
