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
    private final ApprovalFormMapper mapper;   // ← 변경

    public FormApiController(ApprovalFormMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return mapper.findAllActiveOrderByName().stream()
            .map(f -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("code", f.getCode());
                m.put("name", f.getName());
                return m;
            }).toList();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String code){
        var f = mapper.findByCodeAndActiveTrue(code)
            .orElseThrow(() -> new IllegalArgumentException("Form not found: " + code));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", f.getCode());
        body.put("name", f.getName());
        body.put("contentTemplate", f.getContentTemplate());
        return ResponseEntity.ok(body);
    }
}
