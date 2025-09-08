package com.example.demo.repository.mybatis;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalInboxVO;

public interface ApprovalReceiveMapper {
	List<ApprovalInboxVO> selectInbox(
			@Param("loginId") String loginId,
			@Param("status") String status,
			@Param("read") String read,
			@Param("keyword") String keyword,
			@Param("fromDate")LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			@Param("limit") int limit,
			@Param("offset") int offset
			);
	
	long countInbox(
			@Param("loginId") String loginId,
			@Param("status") String status,
			@Param("read") String read,
			@Param("keyword") String keyword,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate
			);
}
