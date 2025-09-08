package com.example.demo.domain;

import lombok.Data;

@Data
public class ApprovalInboxVO {

		private Long docId;
		private String title;
		private String drafterName;
		private String recvStatus;
		private String statusNm;
		private String notifiedAt;
		private String readAt;
		private Integer attachCnt;
}
