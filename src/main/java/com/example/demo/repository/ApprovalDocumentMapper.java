package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalStatus;


@Mapper
public interface ApprovalDocumentMapper {
	int insert(ApprovalDocumentVO doc); //useGeneratedKeys
	ApprovalDocumentVO findById(@Param("id") Long id);
	List<ApprovalDocumentVO> findMyDrafts(@Param("userId") Long userId);
	
	List<ApprovalDocumentVO> findByStatus(@Param("userId") Long userId,
											@Param("status") ApprovalStatus status);
}
