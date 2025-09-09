package com.example.demo.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalLineVO;
import com.example.demo.domain.ApprovalStatus;
import com.example.demo.repository.mybatis.EmpMapper;
import com.example.demo.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApprovalApiController {

    private final ApprovalService approvalService;
    private final EmpMapper empMapper;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long submitMultipart(
            @RequestParam(required = false, name = "docId") Long documentId,
            @RequestParam(name = "formCode") String formCode,
            @RequestParam(name = "title") String documentTitle,
            @RequestParam(name = "content") String documentContent,
            @RequestParam(name = "status") String documentStatus, // 'TEMP' | 'SUBMITTED' (프론트)
            @RequestParam(required = false, name = "approvers") List<Long> approverIdList,
            @RequestParam(required = false, name = "files") List<MultipartFile> uploadedFiles
    ) {
        // 1) 상태 매핑: 'TEMP' | 'SUBMITTED'  ->  ApprovalStatus.DRAFT | ApprovalStatus.SUBMIT
        ApprovalStatus mappedStatus = mapClientStatusToEnum(documentStatus);

        // 2) 문서 VO
        ApprovalDocumentVO approvalDocument = new ApprovalDocumentVO();
        approvalDocument.setDocId(documentId);
        approvalDocument.setFormCode(formCode);
        approvalDocument.setTitle(documentTitle);
        approvalDocument.setContent(documentContent);
        approvalDocument.setStatus(mappedStatus); // ★ enum으로 세팅

        // 3) 결재선 VO 변환
        List<ApprovalLineVO> approvalLineList = new ArrayList<>();
        if (approverIdList != null) {
            int stepNumber = 1;
            for (Long approverId : approverIdList) {
                if (approverId == null) continue;
                ApprovalLineVO line = new ApprovalLineVO();
                line.setApproverId(approverId);
                // 네 VO가 setStepNO 라면 아래 한 줄을 setStepNO로 변경
                line.setStepNo(stepNumber++);
                // line.setStepStatus("SUBMIT");  // ← 이런 메서드 없음. 삭제!
                approvalLineList.add(line);
            }
        }

        // 4) 저장 (임시/상신 분기)
        Long savedDocId = (mappedStatus == ApprovalStatus.SUBMIT)
                ? approvalService.submit(approvalDocument, approvalLineList)
                : approvalService.saveTemp(approvalDocument, approvalLineList);

        // 5) 첨부파일 저장 (없으면 내부에서 바로 return)
        approvalService.saveAttachments(savedDocId, uploadedFiles);
        return savedDocId;
    }

    /* ---------- helpers ---------- */

    // 'TEMP' | 'SUBMITTED' -> ApprovalStatus
    private static ApprovalStatus mapClientStatusToEnum(String clientStatus) {
        if (clientStatus == null) return ApprovalStatus.DRAFT;
        String upper = clientStatus.trim().toUpperCase(Locale.ROOT);
        if ("SUBMITTED".equals(upper)) return ApprovalStatus.SUBMIT;
        if ("TEMP".equals(upper) || "DRAFT".equals(upper)) return ApprovalStatus.DRAFT;
        return ApprovalStatus.DRAFT;
    }
    
 // JSON: 파일 없음일 때
    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long submitJson(@RequestBody Map<String, Object> body) {
        ApprovalStatus mapped = mapClientStatusToEnum( str(body.get("status")) );

        ApprovalDocumentVO doc = new ApprovalDocumentVO();
        doc.setDocId( asLong(body.get("docId")) );
        doc.setFormCode( str(body.get("formCode")) );
        doc.setTitle( str(body.get("title")) );
        doc.setContent( str(body.get("content")) );
        doc.setStatus(mapped);

        List<Long> approverIds = asLongList(body.get("approvers"));
        List<ApprovalLineVO> lines = new ArrayList<>();
        if (approverIds != null) {
            int step = 1;
            for (Long id : approverIds) {
                if (id == null) continue;
                ApprovalLineVO l = new ApprovalLineVO();
                l.setApproverId(id);
                l.setStepNo(step++);     // 프로젝트가 setStepNO 라면 이 한 줄만 변경
                lines.add(l);
            }
        }

        return (mapped == ApprovalStatus.SUBMIT)
                ? approvalService.submit(doc, lines)
                : approvalService.saveTemp(doc, lines);
    }

    /* JSON 변환 헬퍼 (컨트롤러 private 정적 메서드로 같이 넣기) */
    private static String str(Object o){ return o==null? null : String.valueOf(o); }
    private static Long asLong(Object o){
        if (o==null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e) { return null; }
    }
    @SuppressWarnings("unchecked")
    private static List<Long> asLongList(Object o){
        if (o==null) return List.of();
        if (o instanceof List<?> l){
            List<Long> out = new ArrayList<>();
            for (Object e : l){ Long v = asLong(e); if (v!=null) out.add(v); }
            return out;
        }
        Long v = asLong(o);
        return v==null ? List.of() : List.of(v);
    }
    
    @GetMapping("/approvers")
    public List<Map<String, Object>> approvers(@RequestParam(name="q", required=false) String keyword) {
        return empMapper.findApprovers(keyword);
    }
    
    @PostMapping("/approval/{docId:\\d+}/approve")
    public Map<String,Object> approve(@PathVariable long docId) {
      approvalService.approve(docId);
      return Map.of("result", true, "message", "승인되었습니다.");
    }

    @PostMapping("/approval/{docId:\\d+}/reject")
    public Map<String,Object> reject(@PathVariable long docId, @RequestParam(required=false) String reason) {
      approvalService.reject(docId, reason);
      return Map.of("result", true, "message", "반려되었습니다.");
    }   
    
}
