package com.example.demo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApprovalAttachmentVO {
	private Long attId;
	private Long docId;
	private String filename;
	private String path;
	private Long size;
	private String contentType;
	private Long uploadedBy;
	private LocalDateTime createdAt;
}
