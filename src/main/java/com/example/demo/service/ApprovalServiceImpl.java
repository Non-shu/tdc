package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.domain.ApprovalStatus;
import com.example.demo.repository.mybatis.ApprovalDocumentMapper;
import com.example.demo.repository.mybatis.ApprovalFormMapper;
import com.example.demo.repository.mybatis.ApprovalLineMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

  private final ApprovalDocumentMapper docMapper;
  private final ApprovalLineMapper lineMapper;
  private final ApprovalFormMapper formMapper;

  @Override @Transactional
  public long saveTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureFormCode(doc);
    ensureCreatedBy(doc);
    return persistNewDocWithLines(doc, lines, ApprovalStatus.TEMP);
  }

  @Override @Transactional
  public long submit(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureFormCode(doc);
    ensureCreatedBy(doc);
    return persistNewDocWithLines(doc, lines, ApprovalStatus.SUBMITTED);
  }

  private void ensureFormCode(ApprovalDocumentVO doc) {
    if (doc.getFormCode() == null || doc.getFormCode().isBlank()) {
      throw new IllegalArgumentException("formCode is required");
    }
    // 존재/활성 여부 체크 (없으면 FK 위반 나기 전에 400 처리)
    var form = formMapper.findByFormCodeAndActiveTrue(doc.getFormCode());
    if (form == null) {
      throw new IllegalArgumentException("Invalid or inactive formCode: " + doc.getFormCode());
    }
  }

  private void ensureCreatedBy(ApprovalDocumentVO doc) {
    if (doc.getCreatedBy() != null) return;
    var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
    Long uid = 1L;
    if (auth != null && "admin".equalsIgnoreCase(auth.getName())) uid = 1L;
    doc.setCreatedBy(uid);
  }

  @Transactional
  protected long persistNewDocWithLines(ApprovalDocumentVO doc,
                                        List<ApprovalLineVO> lines,
                                        ApprovalStatus status) {
    doc.setStatus(status);
    docMapper.insert(doc);  // insert(form_code, …) — formCode가 NULL이면 바로 터짐

    if (lines != null && !lines.isEmpty()) {
      int i = 1;
      for (ApprovalLineVO l : lines) {
        l.setDocId(doc.getDocId());
        if (l.getStepNo() == null) l.setStepNo(i++);
        if (l.getDecision() == null) l.setDecision("PENDING");
      }
      lineMapper.bulkInsert(lines);
    }
    return doc.getDocId();
  }


  @Override
  @Transactional
  public void updateTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureCreatedBy(doc);
    docMapper.updateTemp(doc);           // XML에 UPDATE 문 필요
    lineMapper.deleteByDocId(doc.getDocId());
    if (lines != null && !lines.isEmpty()) {
      int i = 1;
      for (ApprovalLineVO l : lines) {
        l.setDocId(doc.getDocId());
        if (l.getStepNo() == null) l.setStepNo(i++);
        if (l.getDecision() == null) l.setDecision("PENDING");
      }
      lineMapper.bulkInsert(lines);      // XML에 다중 insert 필요
    }
  }

  @Override
  public Optional<ApprovalDocumentVO> findDoc(long docId) {
    return Optional.ofNullable(docMapper.findById(docId));
  }

}


