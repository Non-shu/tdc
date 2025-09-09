// /static/js/approval-detail.js
(function () {
  const $id = (id) => document.getElementById(id);

  const btnApprove = document.getElementById('btnApprove');
  const btnReject  = document.getElementById('btnReject');

  const csrfHeader = $id('csrfHeader')?.value || null;
  const csrfToken  = $id('csrfToken')?.value  || null;

  async function post(url, body) {
    const headers = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'};
    if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

    const res = await fetch(url, {
      method: 'POST',
      headers,
      body: new URLSearchParams(body || {})
    });
    if (!res.ok) throw new Error('요청 실패');
    return await res.json().catch(() => ({}));
  }

  btnApprove?.addEventListener('click', async (e) => {
    const docId = e.currentTarget.getAttribute('data-docid');
    if (!docId) return;
    if (!confirm('이 문서를 승인하시겠습니까?')) return;

    try {
      const r = await post(`/api/approval/${docId}/approve`);
      alert(r.message || '승인되었습니다.');
      location.reload();
    } catch (err) {
      alert('승인 중 오류가 발생했습니다.');
      console.error(err);
    }
  });

  btnReject?.addEventListener('click', async (e) => {
    const docId = e.currentTarget.getAttribute('data-docid');
    if (!docId) return;
    const reason = prompt('반려 사유를 입력하세요 (선택)');
    if (reason === null) return; // 취소

    try {
      const r = await post(`/api/approval/${docId}/reject`, {reason: reason || ''});
      alert(r.message || '반려되었습니다.');
      location.reload();
    } catch (err) {
      alert('반려 중 오류가 발생했습니다.');
      console.error(err);
    }
  });
})();
