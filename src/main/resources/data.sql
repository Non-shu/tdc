insert into approval_form(code, name, description, content_template, active) values
('F001','지출결의서','경비/지출 품의',
'<p class="t-form-title" style="font-weight:700;margin:4px 0 8px;">지출결의서</p>
<!-- @COST_SHEET rows=20 -->
<p class="muted-12">※ 2행은 항목 헤더, 3행부터 입력 행입니다. 숫자 칸은 오른쪽 정렬됩니다.</p>'
, true);
insert into approval_form(code, name, description, content_template, active) values
('F101','공사계약서(간이)','간이형 계약서',
'<h2 style="text-align:center; margin:8px 0 16px;">공사계약서</h2>
<div class="section-title">제 1 조 [공사개요]</div>
<table class="contract-table"><tbody>
<tr><th style="width:18%;">공사명</th><td>[공사명]</td><th style="width:18%;">공사면적</th><td>[면적]</td></tr>
<tr><th>공사장소</th><td>[주소]</td><th>공사기간</th><td>[착수일] ~ [준공일]</td></tr>
</tbody></table>
<div class="section-title">제 2 조 [공사대금]</div>
<table class="contract-table"><tbody>
<tr><th style="width:20%;">총 공사대금</th><td colspan="3">일금 [총액]원 (부가가치세 [포함/별도])</td></tr>
<tr><th>지급일정(예시)</th><th>지급시기</th><th>금액</th><th>비고</th></tr>
<tr><td>계약금(10%)</td><td>[계약 체결 시]</td><td>[금액]</td><td></td></tr>
<tr><td>1차(35%)</td><td>[골조 시작/자재반입 등]</td><td>[금액]</td><td></td></tr>
<tr><td>2차(35%)</td><td>[공정 80%]</td><td>[금액]</td><td></td></tr>
<tr><td>잔금(20%)</td><td>[사용승인/준공 시]</td><td>[금액]</td><td></td></tr>
</tbody></table>
<div class="section-title">제 3 조 [공사내역]</div>
<ol>
  <li>시공 전 “을”은 “갑”에게 설계도서/시방서/공사내역서 등을 제출하여 승인 받는다.</li>
  <li>자재 규격, 공법 등은 계약 및 관련 법령을 따른다.</li>
  <li>사용자 요구로 인한 추가·변경 시 비용 및 기간 조정 가능하다.</li>
</ol>
<div class="section-title">제 4 조 [공사내용 변경 및 증감]</div>
<p>“갑”의 요구 또는 현장 여건에 의해 변경이 필요한 경우, 쌍방 합의로 증감금액과 공기 조정 후 서면으로 확정한다.</p>
<div class="section-title">제 5 조 [하자보수]</div>
<p>준공 후 [하자담보기간] 내 발생한 하자는 “을”이 무상 보수한다. 천재지변 등 불가항력은 제외한다.</p>
<div class="section-title">제 6 조 [이행지체]</div>
<p>“을”의 책임 사유로 기한 내 완공이 불가한 경우 지연일수 × 공사금액의 [1/1000]을 지체상금으로 한다.</p>
<div class="section-title">제 7 조 [계약의 해지 및 중지]</div>
<p>상호 합의 또는 일방의 중대한 계약위반 시 상대방은 서면 통보로 계약을 해지·중지할 수 있다.</p>
<div class="section-title">제 8 조 [권리 및 의무의 양도]</div>
<p>당사자 일방은 상대방 동의 없이 권리·의무를 제3자에게 양도할 수 없다.</p>
<div class="section-title">제 9 조 [분쟁의 해결]</div>
<p>분쟁 발생 시 상호 협의하고, 해결되지 않을 때는 관할 법원에 제소한다.</p>
<div class="section-title">제 10 조 [특약사항]</div>
<table class="contract-table"><tr><td style="height:140px;">[특약사항 기재]</td></tr></table>
<div class="section-title">제 11 조 [기타 사항]</div>
<ol><li>본 계약서에 명시되지 않은 사항은 관련 법령과 일반 관례를 따른다.</li></ol>
<div class="section-title">서명</div>
<table class="contract-table">
  <thead><tr><th style="width:50%;">발주자 (A)</th><th>수급자 (B)</th></tr></thead>
  <tbody><tr>
    <td>성명 : [ ] &nbsp;&nbsp; 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]<br> 회사명 : [ ]</td>
    <td>성명 : [ ] &nbsp;&nbsp; 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]<br> 회사명 : [ ]</td>
  </tr></tbody>
</table>
<p style="text-align:right;">계약일자 : [YYYY-MM-DD]</p>'
, true);

INSERT INTO approval_form(code, name, description, content_template, active) VALUES
('F102','공사계약서(상세)','상세형 계약서',
'<h2 style="text-align:center; margin:8px 0 16px;">공사 계약서</h2>
<p class="muted-12">※ 아래 빈칸을 채워 계약 내용을 확정하세요. 회사명/담당자/연락처 등은 필요 시 항목 삭제·추가 가능.</p>
<div class="section-title">① 원·하수급인 정보</div>
<div class="party-grid">
  <div>
    <h3 class="muted">원수급인</h3>
    <table class="contract-table"><tbody>
      <tr><th style="width:18%;">상호명</th><td>[ ]</td><th style="width:18%;">대표자</th><td>[ ]</td></tr>
      <tr><th>담당자</th><td>[ ]</td><th>연락처</th><td>[ ]</td></tr>
      <tr><th>사업자등록번호</th><td>[ ]</td><th>이메일</th><td>[ ]</td></tr>
      <tr><th>주소</th><td colspan="3">[ ]</td></tr>
      <tr><th>계좌</th><td colspan="3">[은행/계좌번호/예금주]</td></tr>
    </tbody></table>
  </div>
  <div>
    <h3 class="muted">하수급인</h3>
    <table class="contract-table"><tbody>
      <tr><th style="width:18%;">상호명</th><td>[ ]</td><th style="width:18%;">대표자</th><td>[ ]</td></tr>
      <tr><th>담당자</th><td>[ ]</td><th>연락처</th><td>[ ]</td></tr>
      <tr><th>사업자등록번호</th><td>[ ]</td><th>이메일</th><td>[ ]</td></tr>
      <tr><th>주소</th><td colspan="3">[ ]</td></tr>
      <tr><th>계좌</th><td colspan="3">[은행/계좌번호/예금주]</td></tr>
    </tbody></table>
  </div>
</div>
<div class="section-title">② 공사 내용</div>
<table class="contract-table"><tbody>
  <tr><th style="width:18%;">공사명</th><td>[ ]</td><th style="width:18%;">공사기간</th><td>[착수] ~ [완료]</td></tr>
  <tr><th>공사장소</th><td colspan="3">[ ]</td></tr>
  <tr><th>자재비/인건비</th><td>[포함/제외]</td><th>하자보수기간</th><td>[예: 2년/구조 10년]</td></tr>
</tbody></table>
<div class="section-title">③ 결제 정보</div>
<table class="contract-table">
  <thead><tr><th style="width:22%;">구분</th><th>금액</th><th>지급시기</th><th>비고</th></tr></thead>
  <tbody>
    <tr><td>선급금</td><td>[ ]</td><td>[착수 전]</td><td></td></tr>
    <tr><td>중도금 1</td><td>[ ]</td><td>[ ]</td><td></td></tr>
    <tr><td>중도금 2</td><td>[ ]</td><td>[ ]</td><td></td></tr>
    <tr><td>잔금</td><td>[ ]</td><td>[준공 이후]</td><td></td></tr>
  </tbody>
</table>
<div class="section-title">④ 계약 조항</div>
<ol>
  <li>공사 범위와 사양은 설계도서 및 협의서에 따른다.</li>
  <li>안전/품질/환경관리 기준을 준수하며 필요한 경우 “갑”은 시정조치를 요구할 수 있다.</li>
  <li>불가항력·설계변경 등 합리적 사유가 있을 경우 공기 조정 가능하다.</li>
  <li>하자담보책임 및 보수 절차는 관련 법령과 특약에 따른다.</li>
  <li>비밀유지, 권리·의무 양도 금지, 분쟁 관할법원 등 일반 조항 포함.</li>
</ol>
<div class="section-title">⑤ 특약</div>
<table class="contract-table"><tr><td style="height:140px;">[특약사항 기재]</td></tr></table>
<div class="section-title">서명</div>
<table class="contract-table">
  <thead><tr><th style="width:50%;">원수급인</th><th>하수급인</th></tr></thead>
  <tbody><tr>
    <td>상호 : [ ] / 대표 : [ ] / 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]</td>
    <td>상호 : [ ] / 대표 : [ ] / 연락처 : [ ] &nbsp;&nbsp; <span class="stamp-box"></span><br>주소 : [ ]</td>
  </tr></tbody>
</table>
<p style="text-align:right;">계약일자 : [YYYY-MM-DD]</p>'
, true);


INSERT INTO approval_form(code, name, description, content_template, active)
VALUES ('EST', '견적서', '견적서 기본 양식', '<p>견적서 템플릿</p>', TRUE);

INSERT INTO approval_document(form_id, title, content, status, created_by)
VALUES (1, '[더미] 견적서', '<p> 견적서 더미데이터 </p>', 'SUBMITTED', 1);

INSERT INTO approval_line(doc_id, step_no, approver_id)
VALUES (1, 1, 1001);

INSERT INTO approval_attachment(doc_id, filename, path, size, content_type)
VALUES (1, 'spec.pdf', '/files/spec.pdf', 102400, 'application/pdf');
