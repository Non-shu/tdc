package com.example.demo.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalLineVO;

@Mapper
public interface ApprovalLineMapper {
	
	//문서건 많을 땐 list방식 사용
	void bulkInsert(@Param("list") List<ApprovalLineVO> lines);

    void deleteByDocId(@Param("docId") Long docId);

    List<ApprovalLineVO> selectByDocId(@Param("docId") Long docId);

    int updateStatus(@Param("lineId") Long lineId,
                     @Param("lineStatus") String lineStatus);
}
