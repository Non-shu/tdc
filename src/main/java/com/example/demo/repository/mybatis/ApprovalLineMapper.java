package com.example.demo.repository.mybatis;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.domain.ApprovalLineVO;

@Mapper
public interface ApprovalLineMapper {

  void bulkInsert(@Param("list") List<ApprovalLineVO> lines);

  void deleteByDocId(@Param("docId") Long docId);

  List<ApprovalLineVO> selectByDocId(@Param("docId") Long docId);

  int updateStatus(@Param("lineId") Long lineId,
                   @Param("lineStatus") String lineStatus);

  Integer findMyStepNo(@Param("docId") long docId, @Param("approverId") long approverId);

  // ▼ 순차 강제: 내 step이 “미처리 최솟값”일 때만 1건 갱신되도록
  int approveMyLine(@Param("docId") long docId, @Param("approverId") long approverId);

  int rejectMyLine(@Param("docId") long docId, @Param("approverId") long approverId);

  int countPendingInStep(@Param("docId") long docId, @Param("stepNo") int stepNo);

  // ▼ 다음 단계는 “미처리(SUBMIT/INPROG) 라인이 있는 최솟값”
  Integer findNextStepNo(@Param("docId") long docId, @Param("currentStepNo") int currentStepNo);

  List<ApprovalLineVO> selectWithNames(@Param("docId") long docId);

  boolean canAct(@Param("docId") long docId, @Param("approverId") long approverId);

  int existsMyPendingLine(@Param("docId") Long docId, @Param("empId") Long empId);

  List<ApprovalLineVO> findLinesForDetail(@Param("docId") long docId);
}
