// 안전 실행 (jQuery/ToastUI 로딩 대기)
(function startWhenReady(start){
  function ready(){ return window.jQuery && window.toastui && window.toastui.Editor; }
  if (ready()) return start(window.jQuery);
  let tries = 0, t = setInterval(() => {
    if (ready()){ clearInterval(t); start(window.jQuery); }
    else if (++tries > 200){ clearInterval(t); console.error('[approval-write] deps not ready'); }
  }, 30);
})(function($){
  'use strict';

  let editor = null;
  let approvers = [];
  let files = [];

  const toast = (m)=> alert(m);

  // ---- 헤더 높이를 읽어 sticky-aside 오프셋 변수로 반영
  function setStickyOffsetVar(){
    const header = document.querySelector('.header');
    const h = header ? Math.round(header.getBoundingClientRect().height) : 56;
    document.documentElement.style.setProperty('--header-h', `${h}px`);
  }

  // ---- 에디터 HTML 세팅(초기화 전엔 지연 저장)
  function setEditorHTML(html){
    if (editor && editor.setHTML) editor.setHTML(html || '');
    else window.__DEFERRED_EDITOR_HTML__ = html || '';
  }

  // ---- 템플릿 매크로
  function expandMacros(src){
    if(!src) return '';
    return src.replace(/<!--\s*@COST_SHEET\s+rows=(\d+)\s*-->/gi,
      (_, rows)=> renderCostSheet(parseInt(rows,10) || 20));
  }
  function renderCostSheet(rowCount){
    const headers = ["순번","공정","구분","일자","업체명","품명","규격","단위","수량",
      "재료단가","재료금액","노무단가","노무금액","경비단가","경비금액","합계단가","합계금액","비고"];
    const numeric = new Set([8,9,10,11,12,13,14,15,16]);
    const wrap    = new Set([4,5,6,17]);
    const colgroup = '<colgroup>' + headers.map((_,i)=>{
      const w = (i===0?60:(numeric.has(i)?96:120));
      return `<col style="width:${w}px;min-width:${w}px">`;
    }).join('') + '</colgroup>';
    const thead = '<thead><tr>' + headers.map(h=>`<th>${h}</th>`).join('') + '</tr></thead>';
    let rows = '';
    for(let r=1; r<=rowCount; r++){
      const tds = headers.map((_,ci)=>{
        const align = numeric.has(ci) ? 'text-align:right;' : (ci===0 ? 'text-align:center;' : '');
        const cls   = wrap.has(ci) ? ' class="t-wrap"' : '';
        return `<td style="${align}"${cls}>${ci===0 ? r : ''}</td>`;
      }).join('');
      rows += `<tr>${tds}</tr>`;
    }
    return `<div class="t-scroll"><table data-template="cost-sheet">${colgroup}${thead}<tbody>${rows}</tbody></table></div>`;
  }

  // ---- Toast UI Editor 초기화 + 높이 동적 조절
  function initEditor(){
    const host = document.getElementById('editorHost');
    if(!host){ console.error('editorHost not found'); return; }

    editor = new toastui.Editor({
      el: host,
      height: '520px',           // 초기값 (아래서 즉시 재계산)
      initialEditType: 'wysiwyg',
      previewStyle: 'vertical',
      language: 'ko'
    });
    if (window.__DEFERRED_EDITOR_HTML__) {
      editor.setHTML(window.__DEFERRED_EDITOR_HTML__);
      delete window.__DEFERRED_EDITOR_HTML__;
    }
    resizeEditorToViewport();    // 첫 계산
  }

  // 남은 뷰포트 높이만큼 에디터 높이 설정 (카드 밖으로 안 튀고, 페이지 스크롤 자연스럽게)
  function resizeEditorToViewport(){
    if (!editor) return;
    const host = document.getElementById('editorHost');
    const actions = document.querySelector('.actions-bar');
    const rect = host.getBoundingClientRect();

    const vh = window.innerHeight || document.documentElement.clientHeight;
    const actionsH = actions ? Math.ceil(actions.getBoundingClientRect().height) : 0;

    // header/breadcrumb/여백 등을 고려한 세이프 마진
    const safety = 24 + actionsH + 16; // 아래 여백 포함
    const h = Math.max(420, Math.floor(vh - rect.top - safety));

    editor.setHeight(h + 'px');
  }

  // ---- 도우미
  const debounce = (fn,ms)=>{ let t; return ()=>{ clearTimeout(t); t=setTimeout(fn,ms); }; };

  // ===================== 이벤트/액션 바인딩 =====================
  $(function(){
    setStickyOffsetVar();
    initEditor();

    // 윈도우 리사이즈/사이드바 토글/모달 닫힘 등에서 재계산
    window.addEventListener('resize', debounce(()=>{ setStickyOffsetVar(); resizeEditorToViewport(); }, 100));
    document.addEventListener('hidden.coreui.modal', ()=>{ resizeEditorToViewport(); });

    // ====== 양식 선택 ======
    $(document).on('click', '.btn-select-form', function(){
      const code = $(this).data('code');
      const name = $(this).data('name');
      $.getJSON('/api/forms/' + encodeURIComponent(code))
        .done(function(data){
          const formId = data.formId || data.id || '';
          $('#apprformId').val(formId);
          $('#selectedFormName').text(name || data.name || code);
          const html = expandMacros(data.contentTemplate || '');
          setEditorHTML(html);
          $('#formSelectModal [data-coreui-dismiss="modal"]').trigger('click');
          setTimeout(resizeEditorToViewport, 50);
        })
        .fail(function(xhr){
          toast('양식 로딩 실패: ' + (xhr.responseText || ('HTTP ' + xhr.status)));
        });
    });

    // ====== 결재선 ======
    $(document).on('click', '.btn-add-approver', function(){
      const id   = String($(this).data('approver-id'));
      const name = String($(this).data('approver-name'));
      if (!approvers.some(a=>a.id===id)){ approvers.push({id,name}); renderApproverList(); }
    });
    $(document).on('click', '.btn-remove-approver', function(){
      const id = String($(this).data('approver-id'));
      approvers = approvers.filter(a=>a.id!==id); renderApproverList();
    });
    $('#btnClearApprovers').on('click', ()=>{ approvers=[]; renderApproverList(); });

    function renderApproverList(){
      const html = approvers.map(a =>
        `<li class="list-group-item d-flex justify-content-between align-items-center">
           <span>${a.name}</span>
           <button type="button" class="btn btn-sm btn-outline-secondary btn-remove-approver" data-approver-id="${a.id}">삭제</button>
         </li>`).join('');
      $('#approverList').html(html);
      $('#approverIds').val(approvers.map(a=>a.id).join(','));
      $('#selectedApproverCount').text(approvers.length ? `${approvers.length}명` : '미지정');
    }

    // ====== 파일 ======
    $('#fileInput').on('change', (e)=>{
      files = Array.from(e.target.files || []);
      if (files.length === 0) $('#fileNames').text('선택된 파일 없음');
      else if (files.length === 1) $('#fileNames').text(files[0].name);
      else $('#fileNames').text(`${files[0].name} 외 ${files.length-1}개`);
    });

    // ====== 초기화 ======
    $('#btnReset').on('click', (e)=>{
      e.preventDefault();
      $('#docTitle').val('');
      $('#fileInput').val('');
      $('#apprformId').val('');
      $('#selectedFormName').text('미선택');
      approvers = [];
      renderApproverList();
      if (editor) editor.setHTML('');
      resizeEditorToViewport();
    });

    // ====== 제출/임시저장 ======
    $('#btnSubmit').on('click', ()=> submitDoc('SUBMITTED'));
    $('#btnSaveTemp').on('click', ()=> submitDoc('DRAFT'));

    function validate(status){
      const title = $.trim($('#docTitle').val());
      const formId = $.trim($('#apprformId').val());
      const contentHtml = (editor && editor.getHTML && $.trim(editor.getHTML())) || '';
      if (!title) return {ok:false,msg:'제목을 입력하세요.'};
      if (!formId) return {ok:false,msg:'양식을 선택하세요.'};
      if (status==='SUBMITTED' && approvers.length===0) return {ok:false,msg:'결재선을 지정하세요.'};
      if (!contentHtml) return {ok:false,msg:'본문을 작성하세요.'};
      return {ok:true,title,formId,contentHtml};
    }
    function postJson(url, body){
      return $.ajax({ url, type:'POST', contentType:'application/json; charset=utf-8', data: JSON.stringify(body) });
    }
    function postForm(url, formData){
      return $.ajax({ url, type:'POST', processData:false, contentType:false, data: formData });
    }
    function submitDoc(status){
      const v = validate(status);
      if (!v.ok){ toast(v.msg); return; }
      const hasFiles = files.length > 0;

      if (hasFiles){
        const form = new FormData();
        form.append('title', v.title);
        form.append('content', v.contentHtml);
        form.append('formId', v.formId);
        form.append('status', status);
        approvers.forEach((a,i)=> form.append(`approvers[${i}]`, a.id));
        files.forEach(f => form.append('files', f));
        postForm('/api/approval/submit', form)
          .done(()=> toast(status==='SUBMITTED'?'결재 상신 완료!':'임시 저장 완료!'))
          .fail(xhr=> toast('전송 실패: ' + (xhr.responseText || ('HTTP ' + xhr.status))));
      } else {
        const body = {
          title: v.title,
          content: v.contentHtml,
          formId: Number(v.formId),
          status,
          approvers: approvers.map(a=> Number(a.id))
        };
        postJson('/api/approval/submit', body)
          .done(()=> toast(status==='SUBMITTED'?'결재 상신 완료!':'임시 저장 완료!'))
          .fail(xhr=> toast('전송 실패: ' + (xhr.responseText || ('HTTP ' + xhr.status))));
      }
    }
  });
});
