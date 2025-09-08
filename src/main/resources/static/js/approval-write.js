/*! approval-write.js (clean full rewrite) */
(function () {
  function depsReady() {
    return !!(window.jQuery && window.toastui && window.toastui.Editor);
  }
  var iv = setInterval(function () {
    if (depsReady()) { clearInterval(iv); boot(window.jQuery); }
  }, 30);

  function boot($) {
    'use strict';

    /* ===== 상태 & 공통 ===== */
    var editor = null, approvers = [], files = [];
    var SUBMITTING = false;

    var REDIRECT_AFTER_SUBMIT = '/approval/write';
    var REDIRECT_AFTER_TEMP   = '/approval/write';

    function toast(msg){ alert(msg); }

    // 일반/비동기 모두 지원 디바운스
    function debounce(fn, ms){
      var t; return function(){
        var ctx=this, args=arguments;
        clearTimeout(t);
        t=setTimeout(function(){ fn.apply(ctx,args); }, ms);
      };
    }
    function debounceAsync(fn, ms){
      var t; return function(){
        var ctx=this, args=arguments;
        clearTimeout(t);
        t=setTimeout(async function(){ await fn.apply(ctx,args); }, ms);
      };
    }

    function setSubmitting(on){
      SUBMITTING = !!on;
      $('#btnSubmit,#btnSaveTemp').prop('disabled', on);
    }
    function afterSuccess(status){
      toast(status === 'SUBMITTED' ? '결재 상신 완료!' : '임시 저장 완료!');
      setTimeout(function(){
        window.location.replace(status === 'SUBMITTED' ? REDIRECT_AFTER_SUBMIT : REDIRECT_AFTER_TEMP);
      }, 80);
    }
    function afterFail(xhr){
      var msg = (xhr && xhr.responseJSON && (xhr.responseJSON.message || xhr.responseJSON.error)) ||
                (xhr && xhr.responseText) || ('HTTP ' + (xhr ? xhr.status : ''));
      toast('전송 실패: ' + msg);
    }

    /* ===== Editor helpers ===== */
    function fluidizeEditorContent(){
      var root = document.querySelector('#editorHost .toastui-editor-contents');
      if (!root) return;

      // 1) 기본 표는 고정 레이아웃 (t-fluid 제외)
      root.querySelectorAll('table:not(.t-fluid)').forEach(function(t){
        t.style.width = '100%';
        t.style.tableLayout = 'fixed';
      });

      // 2) t-fluid: 내용에 따라 가로 확장
      root.querySelectorAll('table.t-fluid').forEach(function(t){
        t.style.tableLayout = 'auto';
        t.style.minWidth = '100%';
        t.style.width = 'max-content';
      });

      // 3) 가로 스크롤 컨테이너
      root.querySelectorAll('.t-scroll-x').forEach(function(box){
        box.style.overflowX = 'auto';
        box.style.overflowY = 'hidden';
        box.style.maxWidth = '100%';
        box.style.webkitOverflowScrolling = 'touch';
      });

      // 4) 미디어 반응형
      root.querySelectorAll('img,video,iframe').forEach(function(m){
        m.style.maxWidth = '100%';
        m.style.height = 'auto';
      });

      // 5) 셀 외 요소의 고정 width/height 제거
      root.querySelectorAll('[style*="width"],[style*="height"]').forEach(function (el) {
        var tag = el.tagName;
        if (tag !== 'TD' && tag !== 'TH') {
          el.style.width='100%';
          el.style.maxWidth='100%';
          el.style.removeProperty('height');
        }
      });

      // 6) 셀/행 레이아웃 복원
      root.querySelectorAll('td,th').forEach(function (cell) {
        cell.style.display='table-cell';
        cell.style.float='none';
        cell.style.height='auto';
      });
      root.querySelectorAll('tr').forEach(function (tr) { tr.style.display='table-row'; });

      // 7) 구버전 템플릿 내부 스크롤 무력화
      root.querySelectorAll('.t-scroll').forEach(function(el){
        if (!el.closest('.toastui-editor-defaultUI')) {
          el.style.overflow = 'visible';
          el.style.maxHeight = 'none';
        }
      });
    }

    function forceFillHeights(){
      var host = document.getElementById('editorHost');
      if (!host) return;
      var ui   = host.querySelector('.toastui-editor-defaultUI');
      var main = host.querySelector('.toastui-editor-main');
      var ww   = host.querySelector('.toastui-editor-ww-container');
      var md   = host.querySelector('.toastui-editor-md-container');
      var pm   = host.querySelector('.toastui-editor-ww-container .ProseMirror');

      if (ui)   ui.style.height = '100%';
      if (main) main.style.height = 'calc(100% - 52px)';
      if (ww)   ww.style.height = '100%';
      if (md)   md.style.height = '100%';
      if (pm)   pm.style.minHeight = '100%';
    }

    function autoSizeEditor(){
      try {
        var host = document.getElementById('editorHost');
        if (!host || !editor || !editor.setHeight) return;

        var rect = host.getBoundingClientRect();
        var vpH  = window.innerHeight || document.documentElement.clientHeight;

        // 하단 액션바 위까지 안전 높이
        var bar = document.querySelector('.approval-write-page .actions-bar:last-of-type');
        var SAFE = 220, MIN = 420, MAX = 1600;
        var limitBottom = bar ? bar.getBoundingClientRect().top - 12 : vpH - 12;
        var desired = Math.floor(Math.min(limitBottom, vpH) - rect.top - SAFE);
        var h = Math.max(MIN, Math.min(MAX, desired));

        host.style.height = h + 'px';
        editor.setHeight(h + 'px');
        forceFillHeights();
      } catch(e){ console.warn('[approval-write] autoSizeEditor failed', e); }
    }

    /* ===== 코스트시트 템플릿 (표가 안뜨던 문제 수정) ===== */
    function renderCostSheet(rowCount, fluid){
      var headers = ["순번","공정","구분","일자","업체명","품명","규격","단위","수량",
                     "재료단가","재료금액","노무단가","노무금액","경비단가","경비금액","합계단가","합계금액","비고"];
      var numericIdx = {8:1,9:1,10:1,11:1,12:1,13:1,14:1,15:1,16:1};
      var wrapIdx    = {4:1,5:1,6:1,17:1};

      var colgroup = '<colgroup>' + headers.map(function(_,i){
        var w=(i===0?60:(numericIdx[i]?96:120));
        return '<col style="width:'+w+'px;min-width:'+w+'px">';
      }).join('') + '</colgroup>';

      var thead = '<thead><tr>' + headers.map(function(h){
        return '<th class="t-nowrap">'+h+'</th>';
      }).join('') + '</tr></thead>';

      var rows='';
      for(var r=1;r<=rowCount;r++){
        var tds=headers.map(function(_,ci){
          var align=numericIdx[ci]?'text-align:right;':(ci===0?'text-align:center;':'');
          var cls=wrapIdx[ci]?'':' t-nowrap';
          return '<td style="'+align+'" class="'+cls.trim()+'">'+(ci===0?r:'')+'</td>';
        }).join('');
        rows+='<tr>'+tds+'</tr>';
      }

      var divClass   = fluid ? 't-scroll-x' : 't-scroll';
      var tableClass = fluid ? 't-fluid'    : '';
      return '<div class="'+divClass+'"><table '+(tableClass?'class="'+tableClass+'" ':'')+
             'data-template="cost-sheet">'+colgroup+thead+'<tbody>'+rows+'</tbody></table></div>';
    }

    // 매크로 확장: 주석/태그 모두 지원, fluid 옵션 안전하게 판별
    function expandMacros(src){
      if(!src) return '';

      // 1) 주석 매크로 <!-- @COST_SHEET rows=20 [fluid] -->
      src = src.replace(/<!--\s*@COST_SHEET\b([^>]*)-->/gi, function(match, attrs){
        var mRows = /rows\s*=\s*(\d+)/i.exec(attrs || '');
        var rows  = mRows ? parseInt(mRows[1],10) : 20;
        var hasFluid = /\bfluid\b/i.test(attrs || '');
        return renderCostSheet(rows, hasFluid);
      });

      // 2) 태그 매크로 <cost-sheet rows="20" [fluid] />
      src = src.replace(/<cost-sheet\b([^>]*)\/?>/gi, function(match, attrs){
        var mRows = /rows\s*=\s*["']?(\d+)["']?/i.exec(attrs || '');
        var rows  = mRows ? parseInt(mRows[1],10) : 20;
        var hasFluid = /\bfluid\b/i.test(attrs || '');
        return renderCostSheet(rows, hasFluid);
      });

      return src;
    }

    function initEditor(){
      var host = document.getElementById('editorHost');
      if (!host) return;

      editor = new toastui.Editor({
        el: host,
        height: '540px',
        minHeight: '420px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        language: 'ko'
      });

      forceFillHeights();
      autoSizeEditor();
      setTimeout(forceFillHeights, 0);

      try { editor.on('change', debounce(fluidizeEditorContent, 60)); } catch(_) {}
      setTimeout(fluidizeEditorContent, 0);

      if (window.__DEFERRED_EDITOR_HTML__) {
        setEditorHTML(window.__DEFERRED_EDITOR_HTML__);
        window.__DEFERRED_EDITOR_HTML__ = '';
      }
    }

    function setEditorHTML(html){
      if (editor && editor.setHTML) {
        editor.setHTML(html || '');
        fluidizeEditorContent();
        forceFillHeights();
        autoSizeEditor();
      } else {
        window.__DEFERRED_EDITOR_HTML__ = html || '';
      }
    }

    /* ===== Form code helpers ===== */
    function setFormIdEverywhere(value, name){
      var s = (value == null) ? '' : String(value);
      $('#formId').val(s);
      $('#apprformId').val(s);
      $('#selectedFormName').attr('data-form-id', s).text(name || (s ? s : '미선택'));
    }
    function getFormCode(){
      return ($('#formId').val() || $('#apprformId').val() || $('#selectedFormName').attr('data-form-id') || '').trim();
    }

    /* ===== Validate / Submit ===== */
    function validate(status){
      var title = $.trim($('#docTitle').val());
      var formCode = getFormCode();
      var contentHtml = (editor && editor.getHTML && $.trim(editor.getHTML())) || '';
      if (!title) return {ok:false,msg:'제목을 입력하세요.'};
      if (!formCode) return {ok:false,msg:'양식을 선택하세요.'};
      if (status === 'SUBMITTED' && approvers.length === 0) return {ok:false,msg:'결재선을 지정하세요.'};
      if (!contentHtml) return {ok:false,msg:'본문을 작성하세요.'};
      return {ok:true,title:title,formCode:formCode,contentHtml:contentHtml};
    }

    function postJson(url, body){
      return $.ajax({ url:url, type:'POST', contentType:'application/json; charset=utf-8', data: JSON.stringify(body) });
    }
    function postForm(url, formData){
      return $.ajax({ url:url, type:'POST', processData:false, contentType:false, data: formData });
    }

    function submitDoc(status){
      if (SUBMITTING) return;
      var v = validate(status);
      if (!v.ok){ toast(v.msg); return; }

      var hasFiles = files.length > 0;
      setSubmitting(true);

      if (hasFiles){
        var form = new FormData();
        form.append('title',    v.title);
        form.append('content',  v.contentHtml);
        form.append('formCode', v.formCode);
        form.append('status',   status);
        approvers.forEach(function(a){ form.append('approvers', a.id); });
        files.forEach(function(f){ form.append('files', f); });

        postForm('/api/approval/submit', form)
          .done(function(){ afterSuccess(status); })
          .fail(afterFail)
          .always(function(){ setSubmitting(false); });
      } else {
        var body = {
          title: v.title,
          content: v.contentHtml,
          formCode: v.formCode,
          status: status,
          approvers: approvers.map(function(a){ return Number(a.id); })
        };
        postJson('/api/approval/submit', body)
          .done(function(){ afterSuccess(status); })
          .fail(afterFail)
          .always(function(){ setSubmitting(false); });
      }
    }

    /* ===== Bindings ===== */
    $(function(){
      initEditor();

      var onResizeEditor = debounce(autoSizeEditor, 100);
      window.addEventListener('resize', onResizeEditor);
      window.addEventListener('orientationchange', onResizeEditor);
      window.addEventListener('load', autoSizeEditor);
      window.addEventListener('load', function(){ setTimeout(autoSizeEditor, 120); });
      document.addEventListener('hidden.coreui.modal', onResizeEditor);
      document.addEventListener('shown.coreui.modal',  onResizeEditor);

      // 양식 선택
      $(document).on('click', '.btn-select-form', function(){
        var $btn = $(this);
        var code = $btn.data('code');
        var name = $btn.data('name');

        setFormIdEverywhere(code || '', name);

        if (code) {
          $.getJSON('/api/forms/' + encodeURIComponent(code))
            .done(function (data) {
              var formCode = (data.code || code || '').toString();
              var formName = data.name || name || formCode;
              setFormIdEverywhere(formCode, formName);
              setEditorHTML( expandMacros(data.contentTemplate || '') );
            })
            .fail(afterFail)
            .always(function(){
              var m = document.getElementById('formSelectModal');
              if (m) coreui.Modal.getOrCreateInstance(m).hide();
            });
        } else {
          var m = document.getElementById('formSelectModal');
          if (m) coreui.Modal.getOrCreateInstance(m).hide();
        }
      });

      // 임시저장 / 상신
      $('#btnSaveTemp').on('click', function(e){ e.preventDefault(); if (!SUBMITTING) submitDoc('TEMP'); });
      $('#btnSubmit').on('click',   function(e){ e.preventDefault(); if (!SUBMITTING) submitDoc('SUBMITTED'); });

      // 파일 선택
      $('#fileInput').on('change', function(e){
        files = Array.from(e.target.files || []);
        var names = files.length ? files.map(function(f){return f.name;}).join(', ') : '선택된 파일 없음';
        $('#fileNames').text(names);
      });

      // 초기화
      $(document).off('click', '#btnReset').on('click', '#btnReset', function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (SUBMITTING) return;

        $('#docTitle').val('');
        if (editor && editor.setHTML) editor.setHTML('');

        files = [];
        $('#fileInput').val('');
        $('#fileNames').text('선택된 파일 없음');

        approvers = [];
        $('#approverList').empty();
        $('#approverIds').val('');
        $('#selectedApproverCount').text('미지정');

        setFormIdEverywhere('', '미선택');
        window.__DEFERRED_EDITOR_HTML__ = '';

        forceFillHeights();
        autoSizeEditor();
        $('#docTitle').trigger('focus');
      });

      /* ===== 결재선(서버 조회 - emp 테이블 기반 API) ===== */

      // XSS 안전 이스케이프
      function esc(s){
        return String(s).replace(/[&<>"']/g, function(m){ return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]); });
      }

      // /api/approvers → [{id,name,dept}] (백엔드에서 emp 테이블 조회)
      async function fetchApprovers(keyword) {
        var q = (keyword || '').trim();
        var url = q ? ('/api/approvers?q=' + encodeURIComponent(q)) : '/api/approvers';
        try {
          var res = await fetch(url, { headers: { 'Accept': 'application/json' }});
          if (!res.ok) throw new Error('HTTP ' + res.status);
          return await res.json();
        } catch (e) {
          console.warn('[approvers] fetch failed', e);
          return [];
        }
      }

      function renderEmpRows(list){
        var rows = (list || []).map(function(e){
          return '<tr>'+
                   '<td>'+ esc(e.name) + (e.dept ? ' <span class="text-muted">/ ' + esc(e.dept) + '</span>' : '') + '</td>'+
                   '<td class="text-end">'+
                     '<button class="btn btn-sm btn-outline-primary btn-add-approver" '+
                             'data-approver-id="'+ esc(e.id) +'" '+
                             'data-approver-name="'+ esc(e.name) +'">추가</button>'+
                   '</td>'+
                 '</tr>';
        }).join('');
        $('#empListBody').html(rows);
      }

      // 검색(비동기 디바운스)
      var onEmpSearch = debounceAsync(async function(){
        var kw = $('#empSearch').val();
        renderEmpRows(await fetchApprovers(kw));
      }, 150);

      // 모달 열릴 때 초기 로드
      $(document).on('shown.coreui.modal', '#apprLineModal', async function(){
        $('#empSearch').val('');
        renderEmpRows(await fetchApprovers(''));
      });

      $('#empSearch').on('input', onEmpSearch);

      // 선택/삭제 바인딩
      $(document).on('click', '.btn-add-approver', function () {
        var id   = String($(this).data('approver-id'));
        var name = String($(this).data('approver-name'));
        if (!approvers.some(function(a){ return a.id === id; })) {
          approvers.push({ id:id, name:name });
          renderApproverList();
        }
      });
      $(document).on('click', '.btn-remove-approver', function () {
        var id = String($(this).data('approver-id'));
        approvers = approvers.filter(function(a){ return a.id !== id; });
        renderApproverList();
      });
      $('#btnClearApprovers').on('click', function () {
        approvers = [];
        renderApproverList();
      });

      function renderApproverList(){
        var html = approvers.map(function(a){
          return ''+
            '<li class="list-group-item d-flex justify-content-between align-items-center">'+
              '<span>'+ a.name +'</span>'+
              '<button type="button" class="btn btn-sm btn-outline-secondary btn-remove-approver" data-approver-id="'+ a.id +'">삭제</button>'+
            '</li>';
        }).join('');
        $('#approverList').html(html);
        if ($('#approverIds').length) $('#approverIds').val(approvers.map(function(a){ return a.id; }).join(','));
        if ($('#selectedApproverCount').length) $('#selectedApproverCount').text(approvers.length ? (approvers.length+'명') : '미지정');
      }
    });
  }
})();
