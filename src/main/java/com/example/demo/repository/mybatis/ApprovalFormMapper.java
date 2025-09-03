package com.example.demo.repository.mybatis;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.domain.ApprovalFormVO;

@Mapper
public interface ApprovalFormMapper {
  Optional<ApprovalFormVO> findByCodeAndActiveTrue(@Param("code") String code);
  List<ApprovalFormVO> findAllActiveOrderByName();
}
