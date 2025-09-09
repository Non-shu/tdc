package com.example.demo.repository.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmpMapper {
	 // 로그인 아이디(사번) -> emp_id
    Long findIdByEmpNo(@Param("empNo") String empNo);

    // 결재선 후보 조회 (keyword 없으면 전체)
    List<Map<String, Object>> findApprovers(@Param("keyword") String keyword);
    
    String findEmpNameById(@Param("empId") Long empId);
}
