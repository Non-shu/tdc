-- 양식들: form_code 기준 MERGE
MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES ('F001','지출결의서','경비/지출 품의',
'<p class="t-form-title" style="font-weight:700;margin:4px 0 8px;">지출결의서</p><!-- @COST_SHEET rows=20 --><p class="muted-12">※ 2행은 항목 헤더, 3행부터 입력 행입니다. 숫자 칸은 오른쪽 정렬됩니다.</p>',
TRUE);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES ('F101','공사계약서(간이)','간이형 계약서','<h2 style="text-align:center;">공사계약서</h2>...생략...', TRUE);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES ('F102','공사계약서(상세)','상세형 계약서','<h2 style="text-align:center;">공사 계약서</h2>...생략...', TRUE);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES ('EST','견적서','견적서 기본 양식','<p>견적서 템플릿</p>', TRUE);

-- 샘플 문서 (form_code FK 사용)
INSERT INTO approval_document (form_code, title, content, status, created_by)
VALUES ('F001', '지출결의서 샘플', '<p>샘플 본문</p>', 'SUBMITTED', 1001);

-- 결재선, 첨부 (doc_id=1 가정)
INSERT INTO approval_line (doc_id, step_no, approver_id) VALUES (1, 1, 1001);
INSERT INTO approval_attachment (doc_id, filename, path, size, content_type)
VALUES (1, 'spec.pdf', '/files/spec.pdf', 102400, 'application/pdf');
