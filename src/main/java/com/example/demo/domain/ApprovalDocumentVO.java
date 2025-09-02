package com.example.demo.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocumentVO {
	private Long id;
	private String title;
	private String content;
	private ApprovalStatus status; //Enum
	private Long createdBy;  
	private LocalDateTime createdAt;
}
