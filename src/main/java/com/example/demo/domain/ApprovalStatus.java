package com.example.demo.domain;

public enum ApprovalStatus {
	SUBMIT //상신(대기)
	, INPROG //진행
	, APPROVED //최종승인
	, REJECTED //반려
	, CANCELED //취소
	, DRAFT //임시
}
