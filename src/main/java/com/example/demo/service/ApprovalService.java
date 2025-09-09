package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalInboxVO;
import com.example.demo.domain.ApprovalLineVO;

public interface ApprovalService {
	long saveTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	long submit(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	void updateTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	Optional<ApprovalDocumentVO> findDoc(long docId);
	
	List<ApprovalInboxVO> getInbox(String loginId, String status, String read, String keyword, LocalDate from,LocalDate to, int limit, int offset);
	
	long countInbox(String loginId, String status, String read, String keyword, LocalDate from, LocalDate to);
	
	void saveAttachments(Long docId, List<MultipartFile> files);
	
	void approve(long docId);
	
	void reject(long docId, String reason);
}
