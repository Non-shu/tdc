package com.example.demo.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalStatusHistMapper {
	int insertHist(Long docId, String fromStatus, String toStatus, Long actorId, String memoTx);
}
