package com.example.demo.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocumentVO {
	  private Long docId;  
	  
	  @JsonAlias({"formId", "code", "form_code"})
	  private String formCode;
	  
	  private String title;
	  private String content;
	  private ApprovalStatus status;
	  private Long createdBy;
	  private LocalDateTime createdAt;
}
