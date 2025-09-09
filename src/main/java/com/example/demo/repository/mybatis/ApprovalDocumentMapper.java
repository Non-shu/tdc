package com.example.demo.repository.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalDocumentVO;
import com.example.demo.domain.ApprovalStatus;


@Mapper
public interface ApprovalDocumentMapper {
	int insert(ApprovalDocumentVO doc); //useGeneratedKeys
	ApprovalDocumentVO findById(@Param("docId") Long docId);

	List<ApprovalDocumentVO> findMyDrafts(@Param("userId") Long userId);
	
	List<ApprovalDocumentVO> findByStatus(@Param("userId") Long userId,
											@Param("status") ApprovalStatus status);
	
	int updateTemp(ApprovalDocumentVO doc);
	
	int updateStatus(@Param("docId") long docId, @Param("status") String status);
	
	Map<String,Object> findHeader(@Param("docId") long docId);
	
	Map<String,Object> findHeaderByDocId(@Param("docId") long docId);
}
