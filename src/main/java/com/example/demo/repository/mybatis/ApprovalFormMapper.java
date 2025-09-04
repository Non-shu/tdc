package com.example.demo.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.ApprovalFormVO;

@Mapper
public interface ApprovalFormMapper {
  ApprovalFormVO findByFormCodeAndActiveTrue(@Param("formCode") String formCode);
  List<ApprovalFormVO> findAllActiveOrderByName();
}