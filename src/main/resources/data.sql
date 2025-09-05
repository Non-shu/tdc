-- =========================================================
-- approval_form: 양식 시드 (form_code 기준 MERGE)
--  - F001  : 지출결의서 (표지 + 지출표)
--  - F101  : 공사계약서(간이)
--  - F102  : 공사계약서(상세)
--  - EST   : 견적서 (간단)
-- =========================================================

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES (
  'F001',
  '지출결의서',
  '표지 + 지출비용 내역표',
  '
<p class="t-form-title" style="font-weight:700;margin:4px 0 8px;">지출결의서 (표지)</p>

<table class="contract-table" style="width:100%;border-collapse:collapse;">
  <tbody>
    <tr>
      <th style="width:18%;text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">프로젝트</th>
      <td style="padding:6px;border:1px solid #e9ecef;">[프로젝트명]</td>
      <th style="width:18%;text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">작성자</th>
      <td style="padding:6px;border:1px solid #e9ecef;">[작성자]</td>
    </tr>
    <tr>
      <th style="text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">부서</th>
      <td style="padding:6px;border:1px solid #e9ecef;">[부서]</td>
      <th style="text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">작성일</th>
      <td style="padding:6px;border:1px solid #e9ecef;">[YYYY-MM-DD]</td>
    </tr>
    <tr>
      <th style="text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">지출 목적</th>
      <td colspan="3" style="padding:6px;border:1px solid #e9ecef;">[지출 사유를 간략히 기재]</td>
    </tr>
    <tr>
      <th style="text-align:left;padding:6px;border:1px solid #e9ecef;background:#fafafa;">총 금액</th>
      <td colspan="3" style="padding:6px;border:1px solid #e9ecef;">[합계금액] 원 (부가세 [포함/별도])</td>
    </tr>
  </tbody>
</table>

<div class="pagebreak"></div>

<p class="t-form-title" style="font-weight:700;margin:4px 0 8px;">지출 내역표</p>
<cost-sheet rows="12" mode="fluid" />
<p class="muted-12" style="font-size:12px;margin-top:8px;">※ 2행은 항목 헤더, 3행부터 입력 행입니다. 숫자 칸은 오른쪽 정렬됩니다.</p>
',
  TRUE
);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES (
  'F101',
  '공사계약서(간이)',
  '간이형 계약서',
  '
<h2>공사계약서</h2>
<div class="section-title">제 1 조 [공사개요]</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
  <tr><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사명</th><td style="border:1px solid #e9ecef;padding:6px;">[공사명]</td><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사면적</th><td style="border:1px solid #e9ecef;padding:6px;">[면적]</td></tr>
  <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사장소</th><td style="border:1px solid #e9ecef;padding:6px;">[주소]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사기간</th><td style="border:1px solid #e9ecef;padding:6px;">[착수일] ~ [준공일]</td></tr>
</tbody></table>
<div class="section-title">제 2 조 [공사대금]</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
  <tr><th style="width:20%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">총 공사대금</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">일금 [총액]원 (부가가치세 [포함/별도])</td></tr>
  <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">지급일정(예시)</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">지급시기</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">금액</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">비고</th></tr>
  <tr><td style="border:1px solid #e9ecef;padding:6px;">계약금(10%)</td><td style="border:1px solid #e9ecef;padding:6px;">[계약 체결 시]</td><td style="border:1px solid #e9ecef;padding:6px;">[금액]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
  <tr><td style="border:1px solid #e9ecef;padding:6px;">1차(35%)</td><td style="border:1px solid #e9ecef;padding:6px;">[골조 시작/자재반입 등]</td><td style="border:1px solid #e9ecef;padding:6px;">[금액]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
  <tr><td style="border:1px solid #e9ecef;padding:6px;">2차(35%)</td><td style="border:1px solid #e9ecef;padding:6px;">[공정 80%]</td><td style="border:1px solid #e9ecef;padding:6px;">[금액]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
  <tr><td style="border:1px solid #e9ecef;padding:6px;">잔금(20%)</td><td style="border:1px solid #e9ecef;padding:6px;">[사용승인/준공 시]</td><td style="border:1px solid #e9ecef;padding:6px;">[금액]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
</tbody></table>
<div class="section-title">제 3 조 [공사내역]</div>
<ol>
  <li>시공 전 “을”은 “갑”에게 설계도서/시방서/공사내역서 등을 제출하여 승인 받는다.</li>
  <li>자재 규격, 공법 등은 계약 및 관련 법령을 따른다.</li>
  <li>사용자 요구로 인한 추가·변경 시 비용 및 기간 조정 가능하다.</li>
</ol>
<div class="section-title">제 4 조 [공사내용 변경 및 증감]</div>
<p>“갑” 요구 또는 현장 여건에 의해 변경이 필요한 경우, 증감금액과 공기 조정 후 서면 확정.</p>
<div class="section-title">제 5 조 [하자보수]</div>
<p>준공 후 [하자담보기간] 내 하자 무상 보수(천재지변 제외).</p>
<div class="section-title">제 6 조 [이행지체]</div>
<p>지연일수 × 공사금액의 [1/1000]을 지체상금으로 한다.</p>
<div class="section-title">서명</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;">
  <thead><tr><th style="width:50%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">발주자 (A)</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">수급자 (B)</th></tr></thead>
  <tbody>
    <tr>
      <td style="border:1px solid #e9ecef;padding:6px;">성명 : [ ] &nbsp;&nbsp; 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]<br> 회사명 : [ ]</td>
      <td style="border:1px solid #e9ecef;padding:6px;">성명 : [ ] &nbsp;&nbsp; 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]<br> 회사명 : [ ]</td>
    </tr>
  </tbody>
</table>
<p style="text-align:right;">계약일자 : [YYYY-MM-DD]</p>
',
  TRUE
);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES (
  'F102',
  '공사계약서(상세)',
  '상세형 계약서',
  '
<h2>공사 계약서</h2>
<p class="muted">※ 빈칸을 채워 계약 내용을 확정하세요.</p>
<div class="section-title">① 원·하수급인 정보</div>
<div class="party-grid">
  <div>
    <h6 class="muted">원수급인</h6>
    <table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
      <tr><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">상호명</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">대표자</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">담당자</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">연락처</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">사업자등록번호</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">이메일</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">주소</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">계좌</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">[은행/계좌번호/예금주]</td></tr>
    </tbody></table>
  </div>
  <div>
    <h6 class="muted">하수급인</h6>
    <table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
      <tr><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">상호명</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">대표자</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">담당자</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">연락처</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">사업자등록번호</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">이메일</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">주소</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
      <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">계좌</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">[은행/계좌번호/예금주]</td></tr>
    </tbody></table>
  </div>
</div>

<div class="section-title">② 공사 내용</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
  <tr><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사명</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사기간</th><td style="border:1px solid #e9ecef;padding:6px;">[착수] ~ [완료]</td></tr>
  <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">공사장소</th><td colspan="3" style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
  <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">자재비/인건비</th><td style="border:1px solid #e9ecef;padding:6px;">[포함/제외]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">하자보수기간</th><td style="border:1px solid #e9ecef;padding:6px;">[예: 2년/구조 10년]</td></tr>
</tbody></table>

<div class="section-title">③ 결제 정보</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;">
  <thead><tr><th style="width:22%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">구분</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">금액</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">지급시기</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">비고</th></tr></thead>
  <tbody>
    <tr><td style="border:1px solid #e9ecef;padding:6px;">선급금</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;">[착수 전]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
    <tr><td style="border:1px solid #e9ecef;padding:6px;">중도금 1</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
    <tr><td style="border:1px solid #e9ecef;padding:6px;">중도금 2</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
    <tr><td style="border:1px solid #e9ecef;padding:6px;">잔금</td><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><td style="border:1px solid #e9ecef;padding:6px;">[준공 이후]</td><td style="border:1px solid #e9ecef;padding:6px;"></td></tr>
  </tbody>
</table>

<div class="section-title">④ 계약 조항</div>
<ol>
  <li>설계도서 및 협의서에 따름.</li>
  <li>안전/품질/환경관리 기준 준수.</li>
  <li>불가항력·설계변경 시 공기 조정 가능.</li>
  <li>하자담보책임 등은 관련 법령과 특약에 따름.</li>
</ol>

<div class="section-title">서명</div>
<table class="contract-table" style="width:100%;border-collapse:collapse;">
  <thead><tr><th style="width:50%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">원수급인</th><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">하수급인</th></tr></thead>
  <tbody>
    <tr>
      <td style="border:1px solid #e9ecef;padding:6px;">상호 : [ ] / 대표 : [ ] / 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]</td>
      <td style="border:1px solid #e9ecef;padding:6px;">상호 : [ ] / 대표 : [ ] / 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]</td>
    </tr>
  </tbody>
</table>
<p style="text-align:right;">계약일자 : [YYYY-MM-DD]</p>
',
  TRUE
);

MERGE INTO approval_form (form_code, form_name, description, content_template, active)
KEY(form_code)
VALUES (
  'EST',
  '견적서',
  '견적서 기본 양식',
  '
<p class="t-form-title" style="font-weight:700;margin:4px 0 8px;">견적서</p>
<table class="contract-table" style="width:100%;border-collapse:collapse;"><tbody>
  <tr><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">견적명</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td><th style="width:18%;text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">담당자</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
  <tr><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">유효기간</th><td style="border:1px solid #e9ecef;padding:6px;">[YYYY-MM-DD]</td><th style="text-align:left;border:1px solid #e9ecef;background:#fafafa;padding:6px;">연락처</th><td style="border:1px solid #e9ecef;padding:6px;">[ ]</td></tr>
</tbody></table>
<!-- @COST_SHEET rows=10 -->
',
  TRUE
);


INSERT INTO approval_document (form_code, title, content, status, created_by)
VALUES ('F001', '지출결의서 샘플', '<p>샘플 본문</p>', 'SUBMITTED', 1001);

INSERT INTO approval_line (doc_id, step_no, approver_id) VALUES (1, 1, 1001);
INSERT INTO approval_attachment (doc_id, filename, path, size, content_type)
VALUES (1, 'spec.pdf', '/files/spec.pdf', 102400, 'application/pdf');
