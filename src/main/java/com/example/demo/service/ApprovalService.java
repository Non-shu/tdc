package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalLineVO;

public interface ApprovalService {
	long saveTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	long submit(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	void updateTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines);
	
	Optional<ApprovalDocumentVO> findDoc(long docId);
}
