package com.example.demo.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.mybatis.ApprovalFormMapper;

@RestController
@RequestMapping("/api/forms")
public class FormApiController {
  private final ApprovalFormMapper mapper;

  public FormApiController(ApprovalFormMapper mapper) {
    this.mapper = mapper;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return mapper.findAllActiveOrderByName().stream()
      .map(f -> Map.<String,Object>of(
          "code", f.getFormCode(),
          "name", f.getFormName()))
      .toList();
  }

  @GetMapping("/{code}")
  public ResponseEntity<Map<String,Object>> get(@PathVariable String code) {
    var f = mapper.findByFormCodeAndActiveTrue(code);
    if (f == null) throw new IllegalArgumentException("Form not found: " + code);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", f.getFormCode());
    body.put("name", f.getFormName());
    body.put("contentTemplate", f.getContentTemplate());
    return ResponseEntity.ok(body);
  }
}
