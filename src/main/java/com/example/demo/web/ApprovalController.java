package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;  

@Controller
@RequiredArgsConstructor
@RequestMapping("/approval")
public class ApprovalController {
	
	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("breadcrumb", "결재함");
		return "views/approval/list";
	}
	
	@GetMapping("/write")
	public String write(Model model) {
		model.addAttribute("breadcrumb", "결재 작성");
		return "views/approval/write";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("docId", id);
		return "views/approval/detail";
	}
	
	
}
