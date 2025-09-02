// /static/js/app.js
(function () {
  const btn = document.getElementById('sidebarToggleBtn');
  const sidebarEl = document.getElementById('sidebar');
  if (!btn || !sidebarEl) return;

  // 사이드바 폭 토글 (데스크톱: narrow, 모바일: overlay)
  btn.addEventListener('click', function () {
    try {
      if (window.innerWidth >= 992) {
        document.body.classList.toggle('sidebar-narrow');
      } else if (window.coreui) {
        const inst = coreui.Sidebar.getInstance(sidebarEl) || new coreui.Sidebar(sidebarEl);
        inst.toggle();
      }
    } catch (e) { console.error(e); }
  });

  // CoreUI가 없을 때만 수동 아코디언
  if (!window.coreui) {
    sidebarEl.addEventListener('click', function (e) {
      const toggle = e.target.closest('.nav-link.nav-group-toggle');
      if (!toggle) return;
      e.preventDefault(); e.stopPropagation();
      const group = toggle.closest('.nav-group');
      const on = group.classList.contains('show');
      sidebarEl.querySelectorAll('.nav-group.show').forEach(g => g !== group && g.classList.remove('show'));
      group.classList.toggle('show', !on);
    }, false);
  }
})();
