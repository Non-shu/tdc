package com.example.demo.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmpMapper {
	@Select("""
			SELECT emp_id
			FROM emp
			WHERE emp_no = #{empNo}
			 AND active = 'Y'
			""")
	Long findIdByEmpNo(@Param("empNo")String empNo);
}
