const API = 'http://localhost:8080';
let authHeader = '';
let currentUser = '';
let isAdmin = false;
let allEventos = [];
let allParticipantes = [];

function getHeaders() {
  return { 'Content-Type': 'application/json', 'Authorization': authHeader };
}

async function doLogin() {
  const user = document.getElementById('login-user').value;
  const pass = document.getElementById('login-pass').value;
  authHeader = 'Basic ' + btoa(user + ':' + pass);
  try {
    const res = await fetch(`${API}/eventos/listar`, { headers: getHeaders() });
    if (res.ok || res.status === 403) {
      currentUser = user;
      document.getElementById('sidebar-user').textContent = user;
      document.getElementById('login-screen').style.display = 'none';
      checkAdmin();
      loadAll();
    } else {
      document.getElementById('login-error').style.display = 'block';
    }
  } catch(e) {
    showToast('Erro ao conectar com o servidor', 'error');
  }
}

async function checkAdmin() {
  try {
    const res = await fetch(`${API}/usuario/listar`, { headers: getHeaders() });
    if (res.ok) {
      isAdmin = true;
      // ADMIN vê tudo
      document.getElementById('nav-usuarios').style.display = 'flex';
      document.getElementById('nav-inscricoes').style.display = 'flex';
      document.getElementById('btn-novo-evento').style.display = '';
      document.getElementById('btn-novo-part').style.display = '';
      document.getElementById('btn-nova-insc').style.display = '';
    } else {
      isAdmin = false;
      // USER não vê inscrições nem usuários nem botões de ação
      document.getElementById('nav-usuarios').style.display = 'none';
      document.getElementById('nav-inscricoes').style.display = 'none';
      document.getElementById('btn-novo-evento').style.display = 'none';
      document.getElementById('btn-novo-part').style.display = 'none';
      document.getElementById('btn-nova-insc').style.display = 'none';
    }
  } catch(e) {}
}

function doLogout() {
  authHeader = '';
  document.getElementById('login-screen').style.display = 'flex';
  document.getElementById('login-error').style.display = 'none';
}

function showPage(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById('page-' + page).classList.add('active');
  event.currentTarget.classList.add('active');
  if (page === 'dashboard') loadDashboard();
  if (page === 'eventos') loadEventos();
  if (page === 'participantes') loadParticipantes();
  if (page === 'inscricoes') loadInscricoes();
  if (page === 'usuarios') loadUsuarios();
}

function openModal(id) {
  document.getElementById(id).classList.add('open');
  if (id === 'modal-inscricao') populateSelects();
  if (id === 'modal-participante') populateUsuariosSelect();
}

function closeModal(id) {
  document.getElementById(id).classList.remove('open');
  clearForm(id);
}

function clearForm(modalId) {
  const modal = document.getElementById(modalId);
  modal.querySelectorAll('input, textarea, select').forEach(el => {
    if (el.type !== 'hidden') el.value = '';
  });
  modal.querySelectorAll('input[type=hidden]').forEach(el => el.value = '');
}

function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.className = `toast toast-${type} show`;
  setTimeout(() => t.classList.remove('show'), 3000);
}

async function loadAll() {
  loadDashboard();
  loadEventos();
  loadParticipantes();
}

async function loadDashboard() {
  try {
    const meRes = await fetch(`${API}/usuario/me`, { headers: getHeaders() });
    const me = await meRes.json();
    console.log('usuario/me:', me); // debug

    const loginNome = me.login || me.userName || me.username || currentUser || '?';
    const perfilNum = me.perfil;

    document.getElementById('dash-perfil-user').style.display = 'block';
    document.getElementById('dash-avatar').textContent = loginNome.charAt(0).toUpperCase();
    document.getElementById('dash-nome-user').textContent = loginNome;
    document.getElementById('dash-perfil-badge').textContent = perfilNum === 0 ? 'ADMIN' : 'USER';
    document.getElementById('dash-perfil-badge').className = `badge ${perfilNum === 0 ? 'badge-amber' : 'badge-blue'}`;
    document.getElementById('dash-titulo').textContent = `Olá, ${loginNome}!`;
    document.getElementById('dash-subtitulo').textContent = perfilNum === 0 ? 'Painel administrativo' : 'Seu painel pessoal';

    if (isAdmin) {
      document.getElementById('dash-perfil-user').style.display = 'block';
      document.getElementById('dash-stats-admin').style.display = 'block';
      document.getElementById('dash-stats-user').style.display = 'none';

      const [ev, part, insc, users] = await Promise.all([
        fetch(`${API}/eventos/listar`, { headers: getHeaders() }).then(r => r.json()).catch(() => []),
        fetch(`${API}/participantes/listar`, { headers: getHeaders() }).then(r => r.json()).catch(() => []),
        fetch(`${API}/incricoes/listar`, { headers: getHeaders() }).then(r => r.json()).catch(() => []),
        fetch(`${API}/usuario/listar`, { headers: getHeaders() }).then(r => r.json()).catch(() => []),
      ]);

      document.getElementById('dash-eventos').textContent = Array.isArray(ev) ? ev.length : 0;
      document.getElementById('dash-participantes').textContent = Array.isArray(part) ? part.length : 0;
      document.getElementById('dash-inscricoes').textContent = Array.isArray(insc) ? insc.length : 0;
      document.getElementById('dash-usuarios').textContent = Array.isArray(users) ? users.length : 0;

      const tb = document.getElementById('dash-eventos-table');
      if (Array.isArray(ev) && ev.length > 0) {
        tb.innerHTML = ev.map(e => `<tr>
          <td><strong>${e.nome}</strong></td>
          <td>${e.data || '-'}</td>
          <td>${e.local || '-'}</td>
          <td><span class="badge badge-blue">${e.vagas} vagas</span></td>
        </tr>`).join('');
      } else {
        tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhum evento cadastrado</td></tr>';
      }

    } else {
      document.getElementById('dash-perfil-user').style.display = 'none'; // ✅ esconde para USER
      document.getElementById('dash-stats-admin').style.display = 'none';
      document.getElementById('dash-stats-user').style.display = 'block';

      const evRes = await fetch(`${API}/eventos/listar`, { headers: getHeaders() });
      const todosEventos = await evRes.json();
      document.getElementById('dash-total-eventos-user').textContent = Array.isArray(todosEventos) ? todosEventos.length : 0;

      try {
        const partRes = await fetch(`${API}/participantes/me`, { headers: getHeaders() });
        console.log('participantes/me status:', partRes.status);
        if (partRes.ok) {
          const part = await partRes.json();
          console.log('participante:', part);

          const nomePart = part.nome || '-';
          const emailPart = part.email || '-';

          // ✅ atualiza nome no topo da sidebar e no card
          document.getElementById('sidebar-user').textContent = nomePart;
          document.getElementById('dash-nome-user').textContent = nomePart;
          document.getElementById('dash-avatar').textContent = nomePart.charAt(0).toUpperCase();
          document.getElementById('dash-titulo').textContent = `Olá, ${nomePart}!`;
          document.getElementById('dash-email-user').textContent = emailPart;
          document.getElementById('dash-nome-part').textContent = nomePart;
          document.getElementById('dash-email-part').textContent = emailPart;
          document.getElementById('dash-avatar-part').textContent = nomePart.charAt(0).toUpperCase();
          document.getElementById('dash-sem-participante').style.display = 'none';

          const evInscRes = await fetch(`${API}/incricoes/participantes/${part.id}/eventos`, { headers: getHeaders() });
          const meusEventos = await evInscRes.json();
          document.getElementById('dash-meus-eventos').textContent = Array.isArray(meusEventos) ? meusEventos.length : 0;

          const tb = document.getElementById('dash-meus-eventos-table');
          if (Array.isArray(meusEventos) && meusEventos.length > 0) {
            tb.innerHTML = meusEventos.map(e => `<tr>
              <td><strong>${e.nome}</strong></td>
              <td>${e.data ? new Date(e.data).toLocaleDateString('pt-BR') : '-'}</td>
              <td>${e.local || '-'}</td>
              <td><span class="badge badge-blue">${e.vagas} vagas</span></td>
            </tr>`).join('');
          } else {
            tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:2rem;">Você não está inscrito em nenhum evento</td></tr>';
          }
        } else {
          document.getElementById('dash-sem-participante').style.display = 'block';
          document.getElementById('dash-nome-part').textContent = loginNome;
          document.getElementById('dash-email-part').textContent = '-';
          document.getElementById('dash-avatar-part').textContent = loginNome.charAt(0).toUpperCase();
          document.getElementById('dash-meus-eventos').textContent = '0';
          document.getElementById('dash-meus-eventos-table').innerHTML = '<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhum participante vinculado à sua conta</td></tr>';
        }
      } catch(e) {
        console.error('erro participante/me:', e);
        document.getElementById('dash-sem-participante').style.display = 'block';
      }
    }
  } catch(e) {
    console.error('Erro ao carregar dashboard', e);
  }
}

async function loadEventos() {
  try {
    const res = await fetch(`${API}/eventos/listar`, { headers: getHeaders() });
    allEventos = await res.json();
    renderEventos(allEventos);
  } catch(e) {
    document.getElementById('eventos-table').innerHTML = '<tr><td colspan="5" style="text-align:center;color:#ef4444;padding:2rem;">Erro ao carregar eventos</td></tr>';
  }
}

function renderEventos(list) {
  const tb = document.getElementById('eventos-table');
  if (!Array.isArray(list) || list.length === 0) {
    tb.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhum evento encontrado</td></tr>';
    return;
  }
  tb.innerHTML = list.map(e => `<tr>
    <td><strong>${e.nome}</strong><br><small style="color:#64748b;">${e.descricao || ''}</small></td>
    <td>${e.data || '-'}</td>
    <td>${e.local || '-'}</td>
    <td><span class="badge badge-blue">${e.vagas} vagas</span></td>
    <td style="white-space:nowrap;">
      <button class="btn btn-secondary" style="margin-right:6px;padding:5px 10px;font-size:12px;" onclick="verDetalheEvento(${e.id})">Detalhes</button>
      ${isAdmin ? `<button class="btn btn-secondary" style="margin-right:6px;padding:5px 10px;font-size:12px;" onclick="editEvento(${e.id})">Editar</button>
      <button class="btn btn-danger" style="padding:5px 10px;font-size:12px;" onclick="deleteEvento(${e.id})">Excluir</button>` : ''}
    </td>
  </tr>`).join('');
}

function filterEventos() {
  const q = document.getElementById('search-eventos').value.toLowerCase();
  renderEventos(allEventos.filter(e => e.nome.toLowerCase().includes(q) || (e.local||'').toLowerCase().includes(q)));
}

async function salvarEvento() {
  const id = document.getElementById('evento-id').value;
  const body = {
    nome: document.getElementById('evento-nome').value,
    descricao: document.getElementById('evento-descricao').value,
    data: document.getElementById('evento-data').value,
    local: document.getElementById('evento-local').value,
    vagas: parseInt(document.getElementById('evento-vagas').value)
  };
  if (!body.nome || !body.data || !body.local || !body.vagas) { showToast('Preencha todos os campos obrigatórios', 'error'); return; }
  try {
    const url = id ? `${API}/eventos/${id}` : `${API}/eventos`;
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, { method, headers: getHeaders(), body: JSON.stringify(body) });
    if (res.ok) {
      showToast(id ? 'Evento atualizado!' : 'Evento criado!');
      closeModal('modal-evento');
      loadEventos();
      loadDashboard();
    } else {
      const err = await res.json();
      showToast(err.erro || 'Erro ao salvar', 'error');
    }
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function editEvento(id) {
  try {
    const res = await fetch(`${API}/eventos/${id}`, { headers: getHeaders() });
    const e = await res.json();
    document.getElementById('evento-id').value = e.id;
    document.getElementById('evento-nome').value = e.nome;
    document.getElementById('evento-descricao').value = e.descricao || '';
    document.getElementById('evento-data').value = e.data;
    document.getElementById('evento-local').value = e.local;
    document.getElementById('evento-vagas').value = e.vagas;
    document.getElementById('modal-evento-title').textContent = 'Editar Evento';
    openModal('modal-evento');
  } catch(e) { showToast('Erro ao carregar evento', 'error'); }
}

async function deleteEvento(id) {
  if (!confirm('Deseja excluir este evento?')) return;
  try {
    const res = await fetch(`${API}/eventos/${id}`, { method: 'DELETE', headers: getHeaders() });
    if (res.ok) { showToast('Evento excluído!'); loadEventos(); loadDashboard(); }
    else showToast('Erro ao excluir', 'error');
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function loadParticipantes() {
  try {
    const res = await fetch(`${API}/participantes/listar`, { headers: getHeaders() });
    allParticipantes = await res.json();
    renderParticipantes(allParticipantes);
  } catch(e) {
    document.getElementById('participantes-table').innerHTML = '<tr><td colspan="3" style="text-align:center;color:#ef4444;padding:2rem;">Erro ao carregar participantes</td></tr>';
  }
}

function renderParticipantes(list) {
  const tb = document.getElementById('participantes-table');
  if (!Array.isArray(list) || list.length === 0) {
    tb.innerHTML = '<tr><td colspan="3" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhum participante encontrado</td></tr>';
    return;
  }
  tb.innerHTML = list.map(p => `<tr>
    <td><div style="display:flex;align-items:center;gap:10px;">
      <div style="width:34px;height:34px;border-radius:50%;background:#dbeafe;display:flex;align-items:center;justify-content:center;font-weight:600;color:#1d4ed8;font-size:12px;">${p.nome.charAt(0).toUpperCase()}</div>
      <strong>${p.nome}</strong>
    </div></td>
    <td style="color:#64748b;">${p.email}</td>
    <td>${p.usuario ? `<span class="badge badge-green">${p.usuario.login}</span>` : '<span style="color:#94a3b8;font-size:13px;">Sem vínculo</span>'}</td>
    <td style="white-space:nowrap;">
      <button class="btn btn-secondary" style="margin-right:6px;padding:5px 10px;font-size:12px;" onclick="verDetalheParticipante(${p.id})">Detalhes</button>
      ${isAdmin ? `<button class="btn btn-secondary" style="margin-right:6px;padding:5px 10px;font-size:12px;" onclick="editParticipante(${p.id})">Editar</button>
      <button class="btn btn-danger" style="padding:5px 10px;font-size:12px;" onclick="deleteParticipante(${p.id})">Excluir</button>` : ''}
    </td>
  </tr>`).join('');
}

function filterParticipantes() {
  const q = document.getElementById('search-participantes').value.toLowerCase();
  renderParticipantes(allParticipantes.filter(p => p.nome.toLowerCase().includes(q) || p.email.toLowerCase().includes(q)));
}

async function populateUsuariosSelect() {
  const sel = document.getElementById('part-usuario');
  sel.innerHTML = '<option value="">Sem vínculo</option>';
  try {
    const res = await fetch(`${API}/usuario/listar`, { headers: getHeaders() });
    if (res.ok) {
      const users = await res.json();
      users.forEach(u => {
        sel.innerHTML += `<option value="${u.id}">${u.login} (${u.perfil === 0 ? 'ADMIN' : 'USER'})</option>`;
      });
    }
  } catch(e) {}
}

async function salvarParticipante() {
  const id = document.getElementById('part-id').value;
  const usuarioId = document.getElementById('part-usuario').value;
  const body = {
    nome: document.getElementById('part-nome').value,
    email: document.getElementById('part-email').value,
    usuario: usuarioId ? { id: parseInt(usuarioId) } : null // ✅ vínculo com usuário
  };
  if (!body.nome || !body.email) { showToast('Preencha todos os campos', 'error'); return; }
  try {
    const url = id ? `${API}/participantes/${id}` : `${API}/participantes`;
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, { method, headers: getHeaders(), body: JSON.stringify(body) });
    if (res.ok) {
      showToast(id ? 'Participante atualizado!' : 'Participante criado!');
      closeModal('modal-participante');
      loadParticipantes();
      loadDashboard();
    } else {
      const err = await res.json();
      showToast(err.erro || 'Erro ao salvar', 'error');
    }
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function editParticipante(id) {
  const res = await fetch(`${API}/participantes/${id}`, { headers: getHeaders() });
  const p = await res.json();
  console.log('participante:', p); // debug — veja o que vem do backend

  document.getElementById('part-id').value = p.id;
  document.getElementById('part-nome').value = p.nome;
  document.getElementById('part-email').value = p.email;
  document.getElementById('modal-part-title').textContent = 'Editar Participante';

  document.getElementById('modal-participante').classList.add('open');
  await populateUsuariosSelect();

  if (p.usuario && p.usuario.id) {
    document.getElementById('part-usuario').value = p.usuario.id;
  }
}

async function deleteParticipante(id) {
  if (!confirm('Deseja excluir este participante?')) return;
  try {
    const res = await fetch(`${API}/participantes/${id}`, { method: 'DELETE', headers: getHeaders() });
    if (res.ok) { showToast('Participante excluído!'); loadParticipantes(); loadDashboard(); }
    else showToast('Erro ao excluir', 'error');
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function loadInscricoes() {
  try {
    const res = await fetch(`${API}/incricoes/listar`, { headers: getHeaders() });
    const list = await res.json();
    const tb = document.getElementById('inscricoes-table');
    if (!Array.isArray(list) || list.length === 0) {
      tb.innerHTML = '<tr><td colspan="3" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhuma inscrição encontrada</td></tr>';
      return;
    }
    tb.innerHTML = list.map(i => `<tr>
      <td><span class="badge badge-blue">${i.eventos?.nome || '-'}</span></td>
      <td>${i.participantes?.nome || '-'}</td>
      <td>${isAdmin ? `<button class="btn btn-danger" style="padding:5px 10px;font-size:12px;" onclick="deleteInscricao(${i.id})">Cancelar</button>` : ''}</td>
    </tr>`).join('');
  } catch(e) {
    document.getElementById('inscricoes-table').innerHTML = '<tr><td colspan="3" style="text-align:center;color:#ef4444;padding:2rem;">Erro ao carregar inscrições</td></tr>';
  }
}

async function populateSelects() {
  const evSel = document.getElementById('insc-evento');
  const partSel = document.getElementById('insc-participante');
  evSel.innerHTML = '<option value="">Selecione um evento</option>';
  partSel.innerHTML = '<option value="">Selecione um participante</option>';
  if (allEventos.length > 0) allEventos.forEach(e => evSel.innerHTML += `<option value="${e.id}">${e.nome}</option>`);
  if (allParticipantes.length > 0) allParticipantes.forEach(p => partSel.innerHTML += `<option value="${p.id}">${p.nome}</option>`);
}



async function deleteInscricao(id) {
  if (!confirm('Deseja cancelar esta inscrição?')) return;
  try {
    const res = await fetch(`${API}/incricoes/${id}`, { method: 'DELETE', headers: getHeaders() });
    if (res.ok) { showToast('Inscrição cancelada!'); loadInscricoes(); }
    else showToast('Erro ao cancelar', 'error');
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function loadUsuarios() {
  try {
    const res = await fetch(`${API}/usuario/listar`, { headers: getHeaders() });
    const list = await res.json();
    const tb = document.getElementById('usuarios-table');
    if (!Array.isArray(list) || list.length === 0) {
      tb.innerHTML = '<tr><td colspan="3" style="text-align:center;color:#94a3b8;padding:2rem;">Nenhum usuário encontrado</td></tr>';
      return;
    }
    tb.innerHTML = list.map(u => `<tr>
      <td><div style="display:flex;align-items:center;gap:10px;">
        <div style="width:34px;height:34px;border-radius:50%;background:#fef3c7;display:flex;align-items:center;justify-content:center;font-weight:600;color:#d97706;font-size:12px;">${(u.login||u.username||'U').charAt(0).toUpperCase()}</div>
        <strong>${u.login || u.username || '-'}</strong>
      </div></td>
      <td><span class="badge ${u.perfil === 'ADMIN' || u.perfil === 0 ? 'badge-amber' : 'badge-green'}">${u.perfil === 0 || u.perfil === 'ADMIN' ? 'ADMIN' : 'USER'}</span></td>
      <td style="white-space:nowrap;">
        <button class="btn btn-danger" style="padding:5px 10px;font-size:12px;" onclick="deleteUsuario(${u.id})">Excluir</button>
      </td>
    </tr>`).join('');
  } catch(e) {
    document.getElementById('usuarios-table').innerHTML = '<tr><td colspan="3" style="text-align:center;color:#ef4444;padding:2rem;">Erro ao carregar usuários</td></tr>';
  }
}

async function salvarUsuario() {
  const body = {
    login: document.getElementById('user-login').value,
    senha: document.getElementById('user-senha').value,
    perfil: parseInt(document.getElementById('user-perfil').value)
  };
  if (!body.login || !body.senha) { showToast('Preencha todos os campos', 'error'); return; }
  try {
    const res = await fetch(`${API}/usuario`, { method: 'POST', headers: getHeaders(), body: JSON.stringify(body) });
    if (res.ok) {
      showToast('Usuário criado!');
      closeModal('modal-usuario');
      loadUsuarios();
      loadDashboard();
    } else {
      const err = await res.json();
      showToast(err.erro || 'Erro ao salvar', 'error');
    }
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

async function deleteUsuario(id) {
  if (!confirm('Deseja excluir este usuário?')) return;
  try {
    const res = await fetch(`${API}/usuario/${id}`, { method: 'DELETE', headers: getHeaders() });
    if (res.ok) { showToast('Usuário excluído!'); loadUsuarios(); }
    else showToast('Erro ao excluir', 'error');
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

function showErro(titulo, msg) {
  document.getElementById('modal-erro-titulo').textContent = titulo;
  document.getElementById('modal-erro-msg').textContent = msg;
  openModal('modal-erro');
}

async function verDetalheEvento(id) {
  openModal('modal-detalhe-evento');
  document.getElementById('detalhe-evento-participantes').innerHTML =
    '<tr><td colspan="2" style="text-align:center;color:#94a3b8;padding:1rem;">Carregando...</td></tr>';
  try {
    const [evRes, partRes] = await Promise.all([
      fetch(`${API}/eventos/${id}`, { headers: getHeaders() }),
      fetch(`${API}/incricoes/eventos/${id}/participantes`, { headers: getHeaders() })
    ]);
    const ev = await evRes.json();
    const parts = await partRes.json();

    document.getElementById('detalhe-evento-titulo').textContent = ev.nome;
    document.getElementById('detalhe-evento-nome').textContent = ev.nome || '-';
    document.getElementById('detalhe-evento-data').textContent = ev.data || '-';
    document.getElementById('detalhe-evento-local').textContent = ev.local || '-';
    document.getElementById('detalhe-evento-descricao').textContent = ev.descricao || 'Sem descrição';

    const inscritos = Array.isArray(parts) ? parts.length : 0;
    const vagasDisp = (ev.vagas || 0) - inscritos;
    const vagasEl = document.getElementById('detalhe-evento-vagas');
    vagasEl.textContent = `${vagasDisp} / ${ev.vagas}`;
    vagasEl.style.color = vagasDisp === 0 ? '#ef4444' : vagasDisp <= 3 ? '#f59e0b' : '#22c55e';

    const tb = document.getElementById('detalhe-evento-participantes');
    if (!Array.isArray(parts) || parts.length === 0) {
      tb.innerHTML = '<tr><td colspan="2" style="text-align:center;color:#94a3b8;padding:1rem;">Nenhum participante inscrito</td></tr>';
      return;
    }
    tb.innerHTML = parts.map(p => `<tr>
      <td><div style="display:flex;align-items:center;gap:8px;">
        <div style="width:28px;height:28px;border-radius:50%;background:#dbeafe;display:flex;align-items:center;justify-content:center;font-weight:600;color:#1d4ed8;font-size:11px;">${p.nome.charAt(0).toUpperCase()}</div>
        ${p.nome}
      </div></td>
      <td style="color:#64748b;">${p.email}</td>
    </tr>`).join('');
  } catch(e) {
    document.getElementById('detalhe-evento-participantes').innerHTML =
      '<tr><td colspan="2" style="text-align:center;color:#ef4444;padding:1rem;">Erro ao carregar</td></tr>';
  }
}

async function verDetalheParticipante(id) {
  openModal('modal-detalhe-participante');
  document.getElementById('detalhe-part-eventos').innerHTML =
    '<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:1rem;">Carregando...</td></tr>';
  try {
    const [partRes, evRes] = await Promise.all([
      fetch(`${API}/participantes/${id}`, { headers: getHeaders() }),
      fetch(`${API}/incricoes/participantes/${id}/eventos`, { headers: getHeaders() })
    ]);
    const part = await partRes.json();
    const eventos = await evRes.json();

    document.getElementById('detalhe-part-avatar').textContent = part.nome.charAt(0).toUpperCase();
    document.getElementById('detalhe-part-nome').textContent = part.nome;
    document.getElementById('detalhe-part-email').textContent = part.email;

    const tb = document.getElementById('detalhe-part-eventos');
    if (!Array.isArray(eventos) || eventos.length === 0) {
      tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:1rem;">Nenhum evento encontrado</td></tr>';
      return;
    }
    tb.innerHTML = eventos.map(e => `<tr>
      <td><strong>${e.nome}</strong></td>
      <td>${e.data || '-'}</td>
      <td>${e.local || '-'}</td>
      <td><span class="badge badge-blue">${e.vagas} vagas</span></td>
    </tr>`).join('');
  } catch(e) {
    document.getElementById('detalhe-part-eventos').innerHTML =
      '<tr><td colspan="4" style="text-align:center;color:#ef4444;padding:1rem;">Erro ao carregar</td></tr>';
  }
}

async function salvarInscricao() {
  const eventoId = document.getElementById('insc-evento').value;
  const participanteId = document.getElementById('insc-participante').value;
  if (!eventoId || !participanteId) { showToast('Selecione evento e participante', 'error'); return; }
  try {
    const res = await fetch(`${API}/incricoes?eventoId=${eventoId}&participanteId=${participanteId}`, { method: 'POST', headers: getHeaders() });
    if (res.ok) {
      showToast('Inscrição realizada!');
      closeModal('modal-inscricao');
      loadInscricoes();
      loadDashboard();
    } else {
      const text = await res.text();
      let msg = text;
      try { msg = JSON.parse(text).erro || text; } catch(e) {}
      closeModal('modal-inscricao');
      if (msg.toLowerCase().includes('vaga')) {
        showErro('Sem vagas disponíveis', msg);
      } else if (msg.toLowerCase().includes('inscrito')) {
        showErro('Inscrição duplicada', msg);
      } else {
        showErro('Erro na inscrição', msg);
      }
    }
  } catch(e) { showToast('Erro de conexão', 'error'); }
}

document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', function(e) {
    if (e.target === this) this.classList.remove('open');
  });
});

document.addEventListener('keydown', e => {
  if (e.key === 'Enter' && document.getElementById('login-screen').style.display !== 'none') doLogin();
});