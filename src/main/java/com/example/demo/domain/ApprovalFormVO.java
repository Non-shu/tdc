package com.example.demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApprovalFormVO {
  private String formCode;        // PK
  private String formName;
  private String description;
  private String contentTemplate;
  private String active;
  private LocalDateTime createdAt;
}
