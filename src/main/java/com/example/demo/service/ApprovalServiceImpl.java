package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.ApprovalAttachmentVO;
import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalInboxVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.domain.ApprovalStatus;
import com.example.demo.repository.mybatis.ApprovalAttachmentMapper;
import com.example.demo.repository.mybatis.ApprovalDocumentMapper;
import com.example.demo.repository.mybatis.ApprovalLineMapper;
import com.example.demo.repository.mybatis.ApprovalReceiveMapper;
import com.example.demo.repository.mybatis.ApprovalStatusHistMapper;
import com.example.demo.support.CurrentUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

  private final ApprovalDocumentMapper docMapper;
  private final ApprovalLineMapper lineMapper;
  private final ApprovalReceiveMapper receiveMapper;
  private final ApprovalAttachmentMapper attachmentMapper;
  private final ApprovalStatusHistMapper histMapper;
  private final CurrentUser currentUser;

  // 업로드 경로는 한 곳만 사용 (uploadRoot)
  @Value("${file.upload.dir:/var/app/uploads/approval}")
  private String uploadRoot;

  /* ---------- Public APIs ---------- */

  @Override
  @Transactional
  public long saveTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    Long me = currentUser.id();
    doc.setStatus(ApprovalStatus.DRAFT);
    doc.setCreatedBy(me);
    docMapper.insert(doc);

    upsertLines(doc.getDocId(), lines);
    // 첨부는 컨트롤러에서 받은 파일을 saveAttachments로 저장 (컨트롤러에서 호출)
    histMapper.insertHist(doc.getDocId(), null, ApprovalStatus.DRAFT.name(), me, "임시 저장");
    return doc.getDocId();
  }

  @Override
  @Transactional
  public long submit(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    Long me = currentUser.id();
    String prev = null;

    if (doc.getDocId() == null) {
      doc.setStatus(ApprovalStatus.SUBMIT);
      doc.setCreatedBy(me);
      docMapper.insert(doc);
    } else {
      ApprovalDocumentVO before = docMapper.findById(doc.getDocId());
      prev = (before != null ? before.getStatus().name() : null);

      doc.setStatus(ApprovalStatus.SUBMIT);
      docMapper.updateTemp(doc);          // 제목/본문/양식 갱신
      lineMapper.deleteByDocId(doc.getDocId());
    }

    // 결재선 갱신
    upsertLines(doc.getDocId(), lines);

    // 1차 결재선만 수신함 생성 (멱등)
    receiveMapper.insertFirstStepReceives(doc.getDocId());

    // 이력 기록(선택)
    histMapper.insertHist(doc.getDocId(), prev, ApprovalStatus.SUBMIT.name(), me, "상신");

    return doc.getDocId();
  }

  @Override
  @Transactional
  public void updateTemp(ApprovalDocumentVO doc, List<ApprovalLineVO> lines) {
    docMapper.updateTemp(doc);
    lineMapper.deleteByDocId(doc.getDocId());
    upsertLines(doc.getDocId(), lines);
    // 필요시 이력 추가
    // histMapper.insertHist(doc.getDocId(), "DRAFT", "DRAFT", currentUser.id(), "임시 수정");
  }

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

  /* ---------- Files ---------- */

  @Transactional
  public void saveAttachments(Long docId, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) return;
    Long me = currentUser.id();

    Path docDir = Paths.get(uploadRoot, String.valueOf(docId));
    try {
      Files.createDirectories(docDir);
    } catch (IOException e) {
      throw new RuntimeException("첨부 저장 경로 생성 실패: " + docDir, e);
    }

    List<ApprovalAttachmentVO> toSave = new ArrayList<>();
    for (MultipartFile mf : files) {
      if (mf.isEmpty()) continue;

      String original = Path.of(mf.getOriginalFilename()).getFileName().toString();
      String stored = System.currentTimeMillis() + "_" + original; // 간단 유니크
      Path target = docDir.resolve(stored);
      try {
        mf.transferTo(target.toFile());
      } catch (IOException e) {
        throw new RuntimeException("파일 저장 실패: " + original, e);
      }

      ApprovalAttachmentVO vo = new ApprovalAttachmentVO();
      vo.setDocId(docId);
      vo.setFilename(original);
      vo.setPath(target.toString());
      vo.setSize(mf.getSize());
      vo.setContentType(mf.getContentType() != null ? mf.getContentType() : "application/octet-stream"); // null 가드
      vo.setUploadedBy(me);
      toSave.add(vo);
    }
    if (!toSave.isEmpty()) {
      attachmentMapper.bulkInsert(toSave);
    }
  }

  /* ---------- Helpers ---------- */

  private void upsertLines(Long docId, List<ApprovalLineVO> lines) {
    if (lines == null || lines.isEmpty()) return;
    int step = 1;
    for (ApprovalLineVO l : lines) {
      l.setDocId(docId);
      if (l.getStepNo() == null) l.setStepNo(step++);
      // step_status는 DB DEFAULT 'SUBMIT'
    }
    lineMapper.bulkInsert(lines);
  }
  /* ---------- 승인/반려 -----------*/
  @Override
  @Transactional
  public void approve(long docId) {
    Long me = currentUser.id();

    // 1) 내 단계 확인 (결재 대상자 아니면 예외)
    Integer myStep = lineMapper.findMyStepNo(docId, me);
    if (myStep == null) throw new IllegalStateException("결재 대상자가 아닙니다.");

    // 2) 내 라인 승인 (멱등: 이미 처리돼 있으면 0)
    int changed = lineMapper.approveMyLine(docId, me);
    if (changed == 0) return;

    // 3) 내 수신함 상태 갱신
    receiveMapper.updateReceiveStatus(docId, me, "APPROVED");

    // 4) 같은 단계 미처리 인원 남았는지 확인
    int remain = lineMapper.countPendingInStep(docId, myStep);
    if (remain > 0) {
      docMapper.updateStatus(docId, "INPROG");
      histMapper.insertHist(docId, "INPROG", "INPROG", me, "부분 승인");
      return;
    }

    // 5) 다음 단계 오픈 or 최종 승인
    Integer next = lineMapper.findNextStepNo(docId, myStep);
    if (next != null) {
      receiveMapper.insertReceivesForStep(docId, next);
      docMapper.updateStatus(docId, "INPROG");
      histMapper.insertHist(docId, "INPROG", "INPROG", me, "다음 단계 오픈");
    } else {
      docMapper.updateStatus(docId, "APPROVED");
      histMapper.insertHist(docId, "INPROG", "APPROVED", me, "최종 승인");
    }
  }

  @Override
  @Transactional
  public void reject(long docId, String reason) {
    Long me = currentUser.id();

    Integer myStep = lineMapper.findMyStepNo(docId, me);
    if (myStep == null) throw new IllegalStateException("결재 대상자가 아닙니다.");

    int changed = lineMapper.rejectMyLine(docId, me);
    if (changed == 0) return;

    receiveMapper.updateReceiveStatus(docId, me, "REJECTED");
    // 남아있는 대기 수신 닫기
    receiveMapper.closePendingReceives(docId, "REJECTED");

    docMapper.updateStatus(docId, "REJECTED");
    histMapper.insertHist(docId, "INPROG", "REJECTED", me, (reason == null ? "반려" : reason));
  }
}
