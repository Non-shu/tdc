package com.example.demo.web;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ApprovalInboxVO;
import com.example.demo.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receive")
public class ReceiveApiController {
	private final ApprovalService Service;

	@GetMapping
	public Map<String, Object> list(@AuthenticationPrincipal User me, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int perPage, @RequestParam(required = false) String status,
			@RequestParam(required = false) String read, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {

		int offset = (page - 1) * perPage;
		String loginId = me.getUsername(); // (Principal이 login_id 기준)

		List<ApprovalInboxVO> rows = Service.getInbox(loginId, status, read, keyword, from, to, perPage, offset);
		long total = Service.countInbox(loginId, status, read, keyword, from, to);

		Map<String, Object> pagination = Map.of("page", page, "totalCount", total);
		Map<String, Object> data = Map.of("contents", rows, "pagination", pagination);

		Map<String, Object> res = new HashMap<>();
		res.put("result", true);
		res.put("data", data);
		return res;
	}
}
