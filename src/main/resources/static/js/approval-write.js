/*! approval-write.js - code-based form selection (jQuery only)
 *  - 이중 클릭 방지
 *  - 공통 성공/실패 콜백
 *  - 상신/임시저장 후 리다이렉트
 */
(function () {
  function depsReady() { return !!(window.jQuery && window.toastui && window.toastui.Editor); }
  var iv = setInterval(function () { if (depsReady()) { clearInterval(iv); boot(window.jQuery); } }, 30);

  function boot($) {
    'use strict';

    /* ===== 상태 & 공통 설정 ===== */
    var editor = null, approvers = [], files = [];
    var SUBMITTING = false; // 이중 클릭 방지 플래그

    // 완료 후 이동 경로
    var REDIRECT_AFTER_SUBMIT = '/approval/write';   // 상신 완료 후
    var REDIRECT_AFTER_TEMP   = '/approval/write';  // 임시저장 후

    function toast(msg){ alert(msg); }
    function debounce(fn, ms){ var t; return function(){ clearTimeout(t); t = setTimeout(fn, ms); }; }
    function setSubmitting(on){
      SUBMITTING = !!on;
      $('#btnSubmit,#btnSaveTemp').prop('disabled', on);
    }
    function afterSuccess(status){
      toast(status === 'SUBMITTED' ? '결재 상신 완료!' : '임시 저장 완료!');
      setTimeout(function(){
        var url = (status === 'SUBMITTED') ? REDIRECT_AFTER_SUBMIT : REDIRECT_AFTER_TEMP;
        window.location.replace(url);
      }, 80);
    }
    function afterFail(xhr){
      var msg = (xhr && xhr.responseJSON && (xhr.responseJSON.message || xhr.responseJSON.error)) ||
                (xhr && xhr.responseText) || ('HTTP ' + (xhr ? xhr.status : ''));
      toast('전송 실패: ' + msg);
    }

    /* ===== Editor ===== */
    function fluidizeEditorContent() {
      var root = document.querySelector('#editorHost .toastui-editor-contents');
      if (!root) return;
      root.querySelectorAll('table').forEach(function(t){ t.style.width='100%'; t.style.tableLayout='fixed'; });
      root.querySelectorAll('img,video,iframe').forEach(function(m){ m.style.maxWidth='100%'; m.style.height='auto'; });
      root.querySelectorAll('[style*="width"],[style*="height"]').forEach(function(el){
        var tag = el.tagName;
        if (tag !== 'TD' && tag !== 'TH') { el.style.width='100%'; el.style.maxWidth='100%'; el.style.removeProperty('height'); }
      });
    }
    function forceFillHeights() {
      var host = document.getElementById('editorHost');
      if (!host) return;
      var ui   = host.querySelector('.toastui-editor-defaultUI');
      var main = host.querySelector('.toastui-editor-main');
      var ww   = host.querySelector('.toastui-editor-ww-container');
      var md   = host.querySelector('.toastui-editor-md-container');
      var pm   = host.querySelector('.toastui-editor-ww-container .ProseMirror');
      if (ui)   ui.style.height   = '100%';
      if (main) main.style.height = 'calc(100% - 52px)';
      if (ww)   ww.style.height   = '100%';
      if (md)   md.style.height   = '100%';
      if (pm)   pm.style.minHeight= '100%';
    }
    function autoSizeEditor() {
      try {
        var host = document.getElementById('editorHost');
        if (!host || !editor || !editor.setHeight) return;
        var rect = host.getBoundingClientRect();
        var vpH  = window.innerHeight || document.documentElement.clientHeight;
        var bar  = document.querySelector('.actions-bar');
        var SAFE = 190, MIN = 420, MAX = 1600;
        var limitBottom = bar ? bar.getBoundingClientRect().top - 12 : vpH - 12;
        var desired = Math.floor(Math.min(limitBottom, vpH) - rect.top - SAFE);
        var h = Math.max(MIN, Math.min(MAX, desired));
        host.style.height = h + 'px';
        editor.setHeight(h + 'px');
        forceFillHeights();
      } catch (e) { console.warn('[approval-write] autoSizeEditor failed', e); }
    }
    function renderCostSheet(rowCount){
      var headers = ["순번","공정","구분","일자","업체명","품명","규격","단위","수량","재료단가","재료금액","노무단가","노무금액","경비단가","경비금액","합계단가","합계금액","비고"];
      var numericIdx = {8:1,9:1,10:1,11:1,12:1,13:1,14:1,15:1,16:1};
      var wrapIdx    = {4:1,5:1,6:1,17:1};
      var colgroup = '<colgroup>' + headers.map(function(_,i){ var w=(i===0?60:(numericIdx[i]?96:120)); return '<col style="width:'+w+'px;min-width:'+w+'px">'; }).join('') + '</colgroup>';
      var thead = '<thead><tr>' + headers.map(function(h){return '<th>'+h+'</th>';}).join('') + '</tr></thead>';
      var rows=''; for(var r=1;r<=rowCount;r++){ var tds=headers.map(function(_,ci){ var align=numericIdx[ci]?'text-align:right;':(ci===0?'text-align:center;':''); var cls=wrapIdx[ci]?' class="t-wrap"':''; return '<td style="'+align+'"'+cls+'>'+(ci===0?r:'')+'</td>'; }).join(''); rows+='<tr>'+tds+'</tr>'; }
      return '<div class="t-scroll"><table data-template="cost-sheet">'+colgroup+thead+'<tbody>'+rows+'</tbody></table></div>';
    }
    function expandMacros(src){
      if(!src) return '';
      return src.replace(/<!--\s*@COST_SHEET\s+rows=(\d+)\s*-->/gi, function(_, rows){ return renderCostSheet(parseInt(rows,10) || 20); });
    }
    function initEditor() {
      var host = document.getElementById('editorHost');
      if (!host) return;
      editor = new toastui.Editor({
        el: host, height: '540px', minHeight: '420px',
        initialEditType: 'wysiwyg', previewStyle: 'vertical', language: 'ko'
      });
      forceFillHeights(); autoSizeEditor(); setTimeout(forceFillHeights, 0);
      try { editor.on('change', debounce(fluidizeEditorContent, 60)); } catch(_) {}
      setTimeout(fluidizeEditorContent, 0);
    }
    function setEditorHTML(html){
      if (editor && editor.setHTML) { editor.setHTML(html||''); fluidizeEditorContent(); forceFillHeights(); autoSizeEditor(); }
      else window.__DEFERRED_EDITOR_HTML__ = html || '';
    }

    /* ===== Form code helpers (코드 문자열을 저장) ===== */
    function setFormIdEverywhere(value, name){
      var s = (value == null) ? '' : String(value);
      $('#formId').val(s);
      $('#apprformId').val(s); // 과거 호환
      $('#selectedFormName').attr('data-form-id', s).text(name || (s ? s : '미선택'));
    }
    function getFormCode(){
      var v = ($('#formId').val() || $('#apprformId').val() || $('#selectedFormName').attr('data-form-id') || '').trim();
      return v; // 코드 문자열
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
    function postJson(url, body){ return $.ajax({ url:url, type:'POST', contentType:'application/json; charset=utf-8', data: JSON.stringify(body) }); }
    function postForm(url, formData){ return $.ajax({ url:url, type:'POST', processData:false, contentType:false, data: formData }); }

    function submitDoc(status){
      if (SUBMITTING) return;        // 이중 클릭 방지
      var v = validate(status);
      if (!v.ok){ toast(v.msg); return; }

      var hasFiles = files.length > 0;
      setSubmitting(true);

      if (hasFiles){
        var form = new FormData();
        form.append('title',    v.title);
        form.append('content',  v.contentHtml);
        form.append('formCode', v.formCode);  // 코드만 보냄
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

      var onResizeEditor = debounce(function(){ autoSizeEditor(); }, 100);
      window.addEventListener('resize', onResizeEditor);
      window.addEventListener('orientationchange', onResizeEditor);
      window.addEventListener('load', autoSizeEditor);
      window.addEventListener('load', function(){ setTimeout(autoSizeEditor, 120); });
      document.addEventListener('hidden.coreui.modal', onResizeEditor);
      document.addEventListener('shown.coreui.modal',  onResizeEditor);

      // 양식 선택 버튼 (data-code, data-name 사용)
      $(document).on('click', '.btn-select-form', function(){
        var $btn = $(this);
        var code = $btn.data('code');  // 코드 문자열
        var name = $btn.data('name');

        // 1) 즉시 선택 표시(코드 저장)
        if (code) setFormIdEverywhere(code, name);
        else setFormIdEverywhere('', name);

        // 2) 템플릿 로드
        if (code){
          $.getJSON('/api/forms/' + encodeURIComponent(code))
            .done(function(data){
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

      // 결재선
      $(document).on('click', '.btn-add-approver', function(){
        var id   = String($(this).data('approver-id'));
        var name = String($(this).data('approver-name'));
        if (!approvers.some(function(a){return a.id===id;})){
          approvers.push({id:id, name:name}); renderApproverList();
        }
      });
      $(document).on('click', '.btn-remove-approver', function(){
        var id = String($(this).data('approver-id'));
        approvers = approvers.filter(function(a){ return a.id!==id; }); renderApproverList();
      });
      $('#btnClearApprovers').on('click', function(){ approvers=[]; renderApproverList(); });

      function renderApproverList(){
        var $list = $('#approverList');
        var html = approvers.map(function(a){
          return '<li class="list-group-item d-flex justify-content-between align-items-center">'+
                 '<span>'+a.name+'</span>'+
                 '<button type="button" class="btn btn-sm btn-outline-secondary btn-remove-approver" data-approver-id="'+a.id+'">삭제</button>'+
                 '</li>';
        }).join('');
        $list.html(html);
        $('#approverIds').val(approvers.map(function(a){return a.id;}).join(','));
        $('#selectedApproverCount').text(approvers.length ? (approvers.length+'명') : '미지정');
      }

      // 파일
      $('#fileInput').on('change', function(e){
        files = Array.from(e.target.files || []);
        if (files.length===0) $('#fileNames').text('선택된 파일 없음');
        else if (files.length===1) $('#fileNames').text(files[0].name);
        else $('#fileNames').text(files[0].name+' 외 '+(files.length-1)+'개');
      });

      // 초기화/전송
      $('#btnReset').on('click', function(e){
        e.preventDefault();
        $('#docTitle').val(''); $('#fileInput').val('');
        setFormIdEverywhere('', '미선택');
        approvers=[]; renderApproverList();
        if (editor) editor.setHTML('');
        autoSizeEditor();
      });
      $('#btnSubmit').on('click',   function(){ submitDoc('SUBMITTED'); });
      $('#btnSaveTemp').on('click', function(){ submitDoc('TEMP'); });
    });
  }
})();
