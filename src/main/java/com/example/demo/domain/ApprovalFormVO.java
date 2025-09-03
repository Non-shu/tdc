package com.example.demo.domain;

import lombok.Data;

@Data
public class ApprovalFormVO {
  private Long formId;
  private String code;
  private String name;
  private String description;
  private String contentTemplate;
  private Boolean active;
  private java.time.LocalDateTime createdAt;
}
