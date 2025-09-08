package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalInboxVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.domain.ApprovalStatus;
import com.example.demo.repository.mybatis.ApprovalDocumentMapper;
import com.example.demo.repository.mybatis.ApprovalFormMapper;
import com.example.demo.repository.mybatis.ApprovalLineMapper;
import com.example.demo.repository.mybatis.ApprovalReceiveMapper;
import com.example.demo.support.CurrentUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

  private final ApprovalDocumentMapper docMapper;
  private final ApprovalLineMapper lineMapper;
  private final ApprovalFormMapper formMapper;
  private final ApprovalReceiveMapper receiveMapper;
  private final CurrentUser currentUser; // ← 로그인 사용자 PK 제공자

  /* --------------------------- 작성 & 상신 --------------------------- */

  @Override
  @Transactional
  public long saveTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureFormCode(doc);
    ensureCreatedBy(doc);
    return persistNewDocWithLines(doc, lines, ApprovalStatus.DRAFT);
  }

  @Override
  @Transactional
  public long submit(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureFormCode(doc);
    ensureCreatedBy(doc);
    return persistNewDocWithLines(doc, lines, ApprovalStatus.SUBMIT);
  }

  /** formCode 존재/활성 여부 체크 */
  private void ensureFormCode(ApprovalDocumentVO doc) {
    if (doc.getFormCode() == null || doc.getFormCode().isBlank()) {
      throw new IllegalArgumentException("formCode is required");
    }
    var form = formMapper.findByFormCodeAndActiveTrue(doc.getFormCode());
    if (form == null) {
      throw new IllegalArgumentException("Invalid or inactive formCode: " + doc.getFormCode());
    }
  }

  /** createdBy 미셋이면 현재 로그인 사용자의 PK로 세팅 */
  private void ensureCreatedBy(ApprovalDocumentVO doc) {
    if (doc.getCreatedBy() == null) {
      doc.setCreatedBy(currentUser.id());
    }
  }

  /** 라인 공통 보정: 문서ID 연결, stepNo/decision 기본값 */
  private void normalizeLines(Long docId, List<ApprovalLineVO> lines) {
    int step = 1;
    for (ApprovalLineVO l : lines) {
      l.setDocId(docId);
      if (l.getStepNo() == null) l.setStepNo(step++);
      if (l.getDecision() == null) l.setDecision("PENDING");
    }
  }

  /** 공통 저장 루틴 */
  @Transactional
  protected long persistNewDocWithLines(ApprovalDocumentVO doc,
                                        List<ApprovalLineVO> lines,
                                        ApprovalStatus status) {
    doc.setStatus(status);
    docMapper.insert(doc); // useGeneratedKeys → docId 세팅됨

    if (lines != null && !lines.isEmpty()) {
      normalizeLines(doc.getDocId(), lines);
      lineMapper.bulkInsert(lines);
    }
    return doc.getDocId();
  }

  /* --------------------------- 임시 수정 --------------------------- */

  @Override
  @Transactional
  public void updateTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    ensureCreatedBy(doc);
    docMapper.updateTemp(doc);              // XML에 UPDATE 문 구현되어 있어야 함
    lineMapper.deleteByDocId(doc.getDocId());
    if (lines != null && !lines.isEmpty()) {
      normalizeLines(doc.getDocId(), lines);
      lineMapper.bulkInsert(lines);
    }
  }

  /* --------------------------- 조회 --------------------------- */

  @Override
  public Optional<ApprovalDocumentVO> findDoc(long docId) {
    return Optional.ofNullable(docMapper.findById(docId));
  }

  @Override
  public List<ApprovalInboxVO> getInbox(String loginId, String status, String read,
                                        String keyword, LocalDate from, LocalDate to,
                                        int limit, int offset) {
    return receiveMapper.selectInbox(loginId, status, read, keyword, from, to, limit, offset);
  }

  @Override
  public long countInbox(String loginId, String status, String read,
                         String keyword, LocalDate from, LocalDate to) {
    return receiveMapper.countInbox(loginId, status, read, keyword, from, to);
  }
}
