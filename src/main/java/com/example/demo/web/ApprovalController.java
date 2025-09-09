package com.example.demo.web;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.ApprovalAttachmentVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.repository.mybatis.ApprovalAttachmentMapper;
import com.example.demo.repository.mybatis.ApprovalDocumentMapper;
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
	private final ApprovalDocumentMapper docMapper;

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
	      Long myEmpId = currentUser.id(); // 로그인 사용자 emp_id

	      // 1) 문서 헤더 + 본문 HTML
	      Map<String, Object> header = docMapper.findHeaderByDocId(docId);

	      // 2) 결재선
	      List<ApprovalLineVO> lines = lineMapper.findLinesForDetail(docId);

	      // 3) 첨부
	      List<ApprovalAttachmentVO> atts = attachmentMapper.findByDocId(docId);

	      // 4) 내 차례 여부
	      boolean canAct = lineMapper.existsMyPendingLine(docId, myEmpId) > 0;

	      model.addAttribute("docId", docId);
	      model.addAttribute("header", header);
	      model.addAttribute("lines", lines);
	      model.addAttribute("attachments", atts);
	      model.addAttribute("canAct", canAct);

	      return "views/approval/detail";
	  }
	
	@GetMapping("/receive")
	public String recerive(Model model) {
		model.addAttribute("breadcrumb","수신함");
		return "views/approval/receive";
	}
	
	
}
