package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.repository.mybatis.ApprovalAttachmentMapper;
import com.example.demo.repository.mybatis.ApprovalLineMapper;
import com.example.demo.support.CurrentUser;

import lombok.RequiredArgsConstructor;  

@Controller
@RequiredArgsConstructor
@RequestMapping("/approval")
public class ApprovalController {
	
	private final com.example.demo.repository.mybatis.ApprovalFormMapper formMapper;
	private final ApprovalAttachmentMapper attachmentMapper;
	private final ApprovalLineMapper lineMapper;
	private final CurrentUser currentUser;

	  @GetMapping("/write")
	  public String write(Model model) {
	    model.addAttribute("forms", formMapper.findAllActiveOrderByName());
	    model.addAttribute("breadcrumb", "문서 작성");
	    return "views/approval/write";
	    }
	  
	  @GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("breadcrumb", "결재함");
		return "views/approval/list";
	}
	

	@GetMapping("/{docId:\\d+}")
	public String detail(@PathVariable Long docId, Model model) {
		Long myEmpId = currentUser.id();
		boolean canAct = lineMapper.existsMyPendingLine(docId, myEmpId) > 0;
		
		model.addAttribute("docId", docId);
		model.addAttribute("breadcrumb", "결재내용상세");
		model.addAttribute("header", java.util.Map.of("title","결재 상세", "subtitle","문서 #"+docId));
		var atts = attachmentMapper.findByDocId(docId);
		model.addAttribute("attachments", atts);
		model.addAttribute("canAct", Boolean.FALSE);
		return "views/approval/detail";
	}
	
	@GetMapping("/receive")
	public String recerive(Model model) {
		model.addAttribute("breadcrumb","수신함");
		return "views/approval/receive";
	}
	
	
}
