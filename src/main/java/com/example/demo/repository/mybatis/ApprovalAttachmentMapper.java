package com.example.demo.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.ApprovalAttachmentVO;

@Mapper
public interface ApprovalAttachmentMapper {
	  // 생성
    int insert(ApprovalAttachmentVO vo);
    int bulkInsert(List<ApprovalAttachmentVO> list);

    // 조회
    ApprovalAttachmentVO findById(long attId);
    List<ApprovalAttachmentVO> findByDocId(long docId);
    Long countByDocId(long docId);

    // 다운로드용 메타만 필요할 때(선택)
    ApprovalAttachmentVO findMeta(long attId);

    // 삭제
    int deleteById(long attId);
    int deleteByDocId(long docId);
}
