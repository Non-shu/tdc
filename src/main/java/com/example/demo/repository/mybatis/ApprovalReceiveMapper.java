package com.example.demo.repository.mybatis;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalInboxVO;

@Mapper
public interface ApprovalReceiveMapper {
	List<ApprovalInboxVO> selectInbox(@Param("loginId") String loginId, @Param("status") String status,
			@Param("read") String read, @Param("keyword") String keyword, @Param("from") LocalDate from,
			@Param("to") LocalDate to, @Param("limit") int limit, @Param("offset") int offset);

	long countInbox(@Param("loginId") String loginId, @Param("status") String status, @Param("read") String read,
			@Param("keyword") String keyword, @Param("from") LocalDate from, @Param("to") LocalDate to);

	int insertReceive(@Param("docId") long docId, @Param("approverId") long approverId,
			@Param("recvStatus") String recvStatus);

	int insertFirstStepReceives(@Param("docId") long docId);

	int updateReceiveStatus(@Param("docId") long docId, @Param("approverId") long approverId,
			@Param("status") String status);

	int insertReceivesForStep(@Param("docId") long docId, @Param("stepNo") int stepNo);

	int closePendingReceives(@Param("docId") long docId, @Param("status") String status);
}
