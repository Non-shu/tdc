package com.example.demo.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalApiController {

  private final ApprovalService approvalService;

  // ===== JSON 제출 =====
  @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Long submitJson(@RequestBody Map<String, Object> p) {
    ApprovalDocumentVO doc = new ApprovalDocumentVO();
    doc.setFormCode(String.valueOf(p.get("formCode")));
    doc.setTitle(String.valueOf(p.get("title")));
    doc.setContent(String.valueOf(p.get("content")));

    @SuppressWarnings("unchecked")
    List<Object> approverIds = (List<Object>) p.get("approvers");
    List<ApprovalLineVO> lines = new ArrayList<>();
    if (approverIds != null) {
      int step = 1;
      for (Object o : approverIds) {
        ApprovalLineVO line = new ApprovalLineVO();
        line.setApproverId(Long.parseLong(String.valueOf(o)));
        line.setStepNo(step++);
        lines.add(line);
      }
    }

    String status = String.valueOf(p.getOrDefault("status", "TEMP"));
    if ("SUBMITTED".equalsIgnoreCase(status)) {
      return approvalService.submit(doc, lines);
    } else {
      return approvalService.saveTemp(doc, lines);
    }
  }

  // ===== 파일 첨부가 있는 multipart/form-data 제출 =====
  @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Long submitMultipart(
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam String formCode,
      @RequestParam(defaultValue = "TEMP") String status,
      // 프런트에서 같은 키 이름을 여러 번 append하는 방식(권장)
      @RequestParam( required = false) List<Long> approvers,
      @RequestParam( required = false) List<MultipartFile> files
  ) {
    ApprovalDocumentVO doc = new ApprovalDocumentVO();
    doc.setFormCode(formCode);
    doc.setTitle(title);
    doc.setContent(content);

    List<ApprovalLineVO> lines = new ArrayList<>();
    if (approvers != null) {
      int step = 1;
      for (Long id : approvers) {
        ApprovalLineVO line = new ApprovalLineVO();
        line.setApproverId(id);
        line.setStepNo(step++);
        lines.add(line);
      }
    }

    Long docId = "SUBMITTED".equalsIgnoreCase(status)
        ? approvalService.submit(doc, lines)
        : approvalService.saveTemp(doc, lines);

    // TODO: files != null 이면 여기서 첨부 저장 로직 추가
    // attachmentService.saveAll(docId, files);

    return docId;
  }
}
