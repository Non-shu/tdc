package com.example.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalStatus;
import com.example.demo.repository.ApprovalDocumentMapper;

import lombok.RequiredArgsConstructor;  

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {
	private final ApprovalDocumentMapper mapper;	
	
	
	@PostMapping("/draft")
	public Long draft(@RequestBody ApprovalDocumentVO vo) {
	    vo.setStatus(ApprovalStatus.DRAFT);
	    vo.setCreatedBy(vo.getCreatedBy() == null ? 1L : vo.getCreatedBy());
	    mapper.insert(vo);
	    return vo.getId();
	}

	
	@GetMapping("/{id}")
	public ApprovalDocumentVO get(@PathVariable Long id) {
		return mapper.findById(id);
	}
}
