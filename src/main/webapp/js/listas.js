// listas.js
console.log('listas.js loaded');

function endpoint(path) {
  if (window.APP_CTX && path.startsWith('/')) return window.APP_CTX + path;
  return path;
}

// Si ya hay una implementación global (desde catalogo.js), no sobrescribir
if (typeof window.addToListAjax !== 'function') {
  window.addToListAjax = function(contenidoId, listaName, btnEl) {
    if (btnEl && (btnEl.classList.contains('added') || btnEl.disabled)) return;
    if (btnEl) btnEl.disabled = true;
    fetch(endpoint('/lista/add'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `contenidoId=${encodeURIComponent(contenidoId)}&lista=${encodeURIComponent(listaName)}`
    })
    .then(resp => { if (resp.ok) return resp.json(); throw new Error('HTTP'); })
    .then(() => {
      if (btnEl) {
        btnEl.classList.add('added');
        btnEl.textContent = '✔ Agregado';
        btnEl.disabled = true;
      }
      // No popups: opcionalmente mostrar toast si existe utilidad
      if (typeof window.showToast === 'function') { window.showToast('Agregado a ' + listaName, 'success'); }
      // Refrescar listas si estamos en vista correspondiente
      if (window.location.pathname.endsWith('/mi-lista') && listaName === 'mi-lista') {
        window.location.reload();
      }
      if (window.location.pathname.endsWith('/para-ver') && listaName === 'para-ver') {
        window.location.reload();
      }
    })
    .catch(err => { console.error('addToListAjax error', err); if (typeof window.showToast === 'function') window.showToast('No se pudo agregar a la lista', 'error'); if (btnEl) btnEl.disabled = false; });
  }
}

// Remover sin alerts
window.removeFromListAjax = function(contenidoId, listaName, btnEl) {
  if (btnEl) btnEl.disabled = true;
  fetch(endpoint('/lista/remove'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `contenidoId=${encodeURIComponent(contenidoId)}&lista=${encodeURIComponent(listaName)}`
  })
  .then(resp => { if (resp.ok) return resp.json(); throw new Error('HTTP'); })
  .then(() => {
    if (typeof window.showToast === 'function') { window.showToast('Eliminado de ' + listaName, 'error'); }
    if (window.location.pathname.endsWith('/mi-lista') && listaName === 'mi-lista') {
      window.location.reload();
    }
    if (window.location.pathname.endsWith('/para-ver') && listaName === 'para-ver') {
      window.location.reload();
    }
  })
  .catch(err => { console.error('removeFromListAjax error', err); if (typeof window.showToast === 'function') window.showToast('No se pudo remover de la lista', 'error'); })
  .finally(() => { if (btnEl) btnEl.disabled = false; });
}

window.toggleListState = function(contenidoId, listaName, btn){
  // Si está agregado y está en modo quitar (hover), quita; si no está agregado, agrega
  if (btn.classList.contains('added')){
    if (!btn.classList.contains('remove-mode')) return; // click normal en agregado sin hover, no hace nada
    // Quitar
    btn.disabled = true;
    fetch(endpoint('/lista/remove'), {
      method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `contenidoId=${encodeURIComponent(contenidoId)}&lista=${encodeURIComponent(listaName)}`
    }).then(r=>r.ok?r.json():Promise.reject())
      .then(()=>{
        btn.classList.remove('added');
        btn.classList.remove('remove-mode');
        const labelDefault = btn.querySelector('.label-default');
        if (labelDefault) labelDefault.textContent = (listaName==='mi-lista'?'Mi Lista':'Para Ver');
        if (typeof window.showToast==='function') showToast('Eliminado', 'error');
      })
      .catch(()=>{ if (typeof window.showToast==='function') showToast('Error al quitar','error'); })
      .finally(()=>{ btn.disabled=false; });
  } else {
    // Agregar
    btn.disabled = true;
    fetch(endpoint('/lista/add'), {
      method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'},
      body:`contenidoId=${encodeURIComponent(contenidoId)}&lista=${encodeURIComponent(listaName)}`
    }).then(r=>r.ok?r.json():Promise.reject())
      .then(()=>{
        btn.classList.add('added');
        if (typeof window.showToast==='function') showToast('Agregado', 'success');
      })
      .catch(()=>{ if (typeof window.showToast==='function') showToast('No se pudo agregar','error'); })
      .finally(()=>{ btn.disabled=false; });
  }
}

// Ajustar hover para agregado para entrar a remove-mode
function prepararHoverQuitar(btn, contenidoId, listaName) {
  btn.addEventListener('mouseenter', () => { if (btn.classList.contains('added')) btn.classList.add('remove-mode'); });
  btn.addEventListener('mouseleave', () => { if (btn.classList.contains('added')) btn.classList.remove('remove-mode'); });
}

function initListaButtonsEstado() {
  const ids = Array.from(document.querySelectorAll('button.btn-link[data-contenido]')).map(b=>b.getAttribute('data-contenido'));
  const unique = Array.from(new Set(ids)); if (!unique.length) return;
  fetch(endpoint('/lista/estado'), { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ids:unique}) })
    .then(r=>r.ok?r.json():Promise.reject())
    .then(data=>{
      const mi = new Set(data.miLista||[]); const pv = new Set(data.paraVer||[]);
      document.querySelectorAll('button.btn-link[data-list][data-contenido]').forEach(btn=>{
        const id = btn.getAttribute('data-contenido'); const lista = btn.getAttribute('data-list');
        const agregado = (lista==='mi-lista' && mi.has(id)) || (lista==='para-ver' && pv.has(id));
        if (agregado) btn.classList.add('added'); else btn.classList.remove('added');
        prepararHoverQuitar(btn, id, lista);
      });
    }).catch(()=>{});
}

// ============ Filtrado y Ordenamiento para Para Ver ============
function initFiltrosParaVer() {
  const filtroTipo = document.getElementById('filtroTipo');
  const filtroOrden = document.getElementById('filtroOrden');
  const container = document.querySelector('.movie-row.no-select');

  if (!filtroTipo || !filtroOrden || !container) return;

  function aplicarFiltros() {
    const tipoSeleccionado = filtroTipo.value;
    const ordenSeleccionado = filtroOrden.value;

    const cards = Array.from(container.querySelectorAll('.movie-card[data-tipo]'));

    // Filtrar por tipo
    cards.forEach(card => {
      const tipo = card.getAttribute('data-tipo');
      if (tipoSeleccionado === 'todas') {
        card.style.display = '';
      } else if (tipoSeleccionado === 'pelicula' && tipo === 'pelicula') {
        card.style.display = '';
      } else if (tipoSeleccionado === 'serie' && tipo === 'serie') {
        card.style.display = '';
      } else {
        card.style.display = 'none';
      }
    });

    // Ordenar
    const visibleCards = cards.filter(c => c.style.display !== 'none');

    if (ordenSeleccionado === 'titulo') {
      visibleCards.sort((a, b) => {
        const tituloA = (a.getAttribute('data-titulo') || '').toLowerCase();
        const tituloB = (b.getAttribute('data-titulo') || '').toLowerCase();
        return tituloA.localeCompare(tituloB, 'es');
      });
    } else {
      // Fecha agregada = orden original
      visibleCards.sort((a, b) => {
        const ordenA = parseInt(a.getAttribute('data-orden') || '0');
        const ordenB = parseInt(b.getAttribute('data-orden') || '0');
        return ordenA - ordenB;
      });
    }

    // Reordenar en el DOM
    visibleCards.forEach(card => container.appendChild(card));
    // Mover los ocultos al final
    cards.filter(c => c.style.display === 'none').forEach(card => container.appendChild(card));
  }

  filtroTipo.addEventListener('change', aplicarFiltros);
  filtroOrden.addEventListener('change', aplicarFiltros);
}

function initParaVer() {
  initListaButtonsEstado();
  initFiltrosParaVer();
}

if (document.readyState==='loading') document.addEventListener('DOMContentLoaded', initParaVer); else initParaVer();
