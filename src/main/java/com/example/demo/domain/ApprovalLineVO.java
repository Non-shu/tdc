// src/main/java/com/example/demo/domain/ApprovalLineVO.java
package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApprovalLineVO {
    private Long lineId;      // approval_line.line_id
    private Long docId;       // approval_line.doc_id
    private Integer stepNo;   // approval_line.step_no
    private Long approverId;  // approval_line.approver_id
    private String decision;  // PENDING / APPROVED / REJECTED
    private LocalDateTime decidedAt;
    private String comment;

    // 조회용(조인 별칭으로 매핑)
    private String approverName;
    private String approverDeptName;
}