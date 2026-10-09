(() => {
  'use strict';

  const $ = (selector) => document.querySelector(selector);
  const state = { page: 0, size: 9, totalPages: 0, token: sessionStorage.getItem('hackhubToken'), account: null };
  const grid = $('#hackathon-grid');

  function make(tag, className, text) {
    const node = document.createElement(tag);
    if (className) node.className = className;
    if (text !== undefined && text !== null) node.textContent = String(text);
    return node;
  }

  function append(parent, child) { parent.appendChild(child); return child; }
  function safeDate(value) {
    if (!value) return 'Not specified';
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? 'Not specified' : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(date);
  }
  function money(value) {
    if (value === undefined || value === null || Number.isNaN(Number(value))) return '—';
    return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(Number(value));
  }
  function statusLabel(status) {
    return ({ IN_REGISTRATION: 'REGISTRATION OPEN', RUNNING: 'IN PROGRESS', IN_EVALUATION: 'EVALUATION', CLOSED: 'CLOSED' })[status] || (status || 'STATUS UNKNOWN').replaceAll('_', ' ');
  }
  function statusCss(status) {
    return ({ IN_REGISTRATION: 'status-registration', READY_TO_START: 'status-registration', RUNNING: 'status-running', IN_EVALUATION: 'status-evaluation', CLOSED: 'status-closed', CREATED: 'status-registration' })[status] || 'status-registration';
  }
  function showNotice(message) {
    const notice = $('#api-notice');
    notice.textContent = message;
    notice.classList.remove('hidden');
  }
  function clearNotice() { $('#api-notice').classList.add('hidden'); }
  function toast(message) {
    const node = $('#toast');
    node.textContent = message;
    node.classList.remove('hidden');
    window.clearTimeout(toast.timer);
    toast.timer = window.setTimeout(() => node.classList.add('hidden'), 4200);
  }

  async function api(path, options = {}) {
    const headers = new Headers(options.headers || {});
    if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
    if (state.token) headers.set('Authorization', `Bearer ${state.token}`);
    const response = await fetch(path, { ...options, headers });
    let payload = null;
    const type = response.headers.get('content-type') || '';
    if (response.status !== 204 && type.includes('json')) {
      try { payload = await response.json(); } catch { payload = null; }
    } else if (response.status !== 204) {
      try { payload = await response.text(); } catch { payload = null; }
    }
    if (!response.ok) {
      const message = payload && typeof payload === 'object'
        ? (payload.detail || payload.message || payload.title || payload.failureReason || `Request failed (${response.status})`)
        : (payload || `Request failed (${response.status})`);
      if (response.status === 401 && state.token) {
        state.token = null;
        state.account = null;
        sessionStorage.removeItem('hackhubToken');
        updateAccountUI();
      }
      throw new Error(message);
    }
    return payload;
  }

  function skeletons() {
    grid.replaceChildren();
    for (let i = 0; i < 6; i++) append(grid, make('div', 'hackathon-card skeleton'));
    grid.setAttribute('aria-busy', 'true');
    $('#empty-state').classList.add('hidden');
  }

  function createHackathonCard(hackathon, index) {
    const card = make('article', 'hackathon-card');
    card.tabIndex = 0;
    card.setAttribute('role', 'button');
    card.setAttribute('aria-label', `View details for ${hackathon.name || 'hackathon'}`);
    const top = append(card, make('div', 'card-top'));
    const status = append(top, make('span', `status-pill ${statusCss(hackathon.status)}`, statusLabel(hackathon.status)));
    const format = hackathon.online ? 'ONLINE' : 'IN PERSON';
    append(top, make('span', 'format-pill', format));
    append(card, make('div', 'card-id', `CHALLENGE / ${String(index + 1 + state.page * state.size).padStart(3, '0')}`));
    append(card, make('h3', '', hackathon.name || 'Untitled hackathon'));
    append(card, make('p', 'card-description', hackathon.regulation || hackathon.type || 'Explore the challenge and gather your team.'));
    const meta = append(card, make('div', 'card-meta'));
    const left = append(meta, make('div'));
    append(left, make('span', 'meta-label', 'Starts')); append(left, make('span', 'meta-value', safeDate(hackathon.dates && hackathon.dates.startAt)));
    const right = append(meta, make('div'));
    append(right, make('span', 'meta-label', 'Prize pool')); append(right, make('span', 'meta-value', money(hackathon.prize)));
    append(meta, make('span', 'card-arrow', '↗'));
    const open = () => openDetails(hackathon.id);
    card.addEventListener('click', open);
    card.addEventListener('keydown', (event) => { if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); open(); } });
    return card;
  }

  function buildSearchUrl() {
    const params = new URLSearchParams({ page: String(state.page), size: String(state.size), sort: 'id,desc' });
    const name = $('#filter-name').value.trim();
    const status = $('#filter-status').value;
    const online = $('#filter-online').value;
    const after = $('#filter-after').value;
    const before = $('#filter-before').value;
    if (name) params.set('nameContains', name);
    if (status) params.set('status', status);
    if (online) params.set('isOnline', online);
    if (after) params.set('startsAfter', after);
    if (before) params.set('startsBefore', before);
    return `/api/hackathons/search?${params}`;
  }

  async function loadHackathons() {
    clearNotice();
    skeletons();
    $('#result-count').textContent = 'LOADING…';
    try {
      const page = await api(buildSearchUrl());
      const items = Array.isArray(page) ? page : (page && Array.isArray(page.content) ? page.content : []);
      state.totalPages = Number(page && page.totalPages) || (items.length ? 1 : 0);
      grid.replaceChildren();
      grid.setAttribute('aria-busy', 'false');
      items.forEach((item, index) => append(grid, createHackathonCard(item, index)));
      $('#result-count').textContent = `${Number(page && page.totalElements) || items.length} OPPORTUNIT${(Number(page && page.totalElements) || items.length) === 1 ? 'Y' : 'IES'}`;
      $('#page-indicator').textContent = `Page ${state.page + 1}${state.totalPages ? ` of ${state.totalPages}` : ''}`;
      $('#previous-page').disabled = state.page <= 0;
      $('#next-page').disabled = state.totalPages > 0 ? state.page + 1 >= state.totalPages : items.length < state.size;
      $('#empty-state').classList.toggle('hidden', items.length > 0);
      if (items.length === 0) grid.replaceChildren();
    } catch (error) {
      grid.replaceChildren();
      grid.setAttribute('aria-busy', 'false');
      $('#result-count').textContent = 'API NOT AVAILABLE';
      $('#page-indicator').textContent = `Page ${state.page + 1}`;
      $('#previous-page').disabled = state.page <= 0;
      $('#next-page').disabled = true;
      $('#empty-state').classList.add('hidden');
      showNotice(`Could not load hackathons. ${error.message}. Check the backend and try again.`);
    }
  }

  function detailMeta(label, value) {
    const item = make('div');
    append(item, make('span', 'meta-label', label));
    append(item, make('span', 'meta-value', value || 'Not specified'));
    return item;
  }

  async function openDetails(id) {
    const dialog = $('#detail-dialog');
    const content = $('#detail-content');
    content.replaceChildren(make('p', 'field-hint', 'Loading challenge details…'));
    dialog.showModal();
    try {
      const hackathon = await api(`/api/hackathons/${encodeURIComponent(id)}`);
      content.replaceChildren();
      const header = append(content, make('div', 'detail-header'));
      const top = append(header, make('div', 'card-top'));
      append(top, make('span', `status-pill ${statusCss(hackathon.status)}`, statusLabel(hackathon.status)));
      append(top, make('span', 'format-pill', hackathon.online ? 'ONLINE' : 'IN PERSON'));
      append(header, make('h2', '', hackathon.name || 'Hackathon details'));
      append(header, make('p', '', hackathon.regulation || hackathon.type || 'No challenge description has been provided.'));
      const data = append(content, make('div', 'detail-data'));
      append(data, detailMeta('Event starts', safeDate(hackathon.dates && hackathon.dates.startAt)));
      append(data, detailMeta('Event ends', safeDate(hackathon.dates && hackathon.dates.endAt)));
      append(data, detailMeta('Registration opens', safeDate(hackathon.subscriptionDates && hackathon.subscriptionDates.startAt)));
      append(data, detailMeta('Registration closes', safeDate(hackathon.subscriptionDates && hackathon.subscriptionDates.endAt)));
      append(data, detailMeta('Location', hackathon.online ? 'Online' : (hackathon.location || 'Not specified')));
      append(data, detailMeta('Prize pool', money(hackathon.prize)));
      append(data, detailMeta('Maximum team size', hackathon.maxTeamSize));
      append(data, detailMeta('Ranking policy', String(hackathon.rankingPolicy || '—').replaceAll('_', ' ')));
      const actions = append(content, make('div', 'detail-actions'));
      const docs = make('a', 'button button-outline', 'Open API docs ↗'); docs.href = '/swagger-ui.html'; docs.target = '_blank'; docs.rel = 'noopener'; append(actions, docs);
      if (state.account) {
        const profileButton = make('button', 'button button-outline', 'My account ↗');
        profileButton.addEventListener('click', () => toast(`Signed in as ${state.account.username} · ${state.account.role}`));
        append(actions, profileButton);
      }
      if (state.account && state.account.role === 'STAFF') {
        const isOrganizer = Number(state.account.id) === Number(hackathon.organizer);
        const isJudge = Number(state.account.id) === Number(hackathon.judge);
        const winnerId = hackathon.winnerParticipatingTeamId ?? hackathon.winnerParticipatingTeam ?? null;
        const payoutStatus = hackathon.prizeStatus ?? (hackathon.prizePayout && hackathon.prizePayout.status) ?? null;
        const workflowAction = (label, route) => {
          const button = make('button', 'button button-primary', `${label} ↗`);
          button.addEventListener('click', async () => {
            button.disabled = true;
            try {
              await api(`/api/hackathons/${encodeURIComponent(id)}/${route}`, { method: 'POST' });
              $('#detail-dialog').close();
              toast(`${label} completed.`);
              await loadHackathons();
            } catch (error) {
              toast(error.message);
            } finally { button.disabled = false; }
          });
          append(actions, button);
        };
        if (isOrganizer && hackathon.status === 'IN_REGISTRATION') workflowAction('Close registration', 'close-registration');
        if (isOrganizer && hackathon.status === 'READY_TO_START') workflowAction('Start event', 'start');
        if (isJudge && hackathon.status === 'RUNNING') workflowAction('Start evaluation', 'start-evaluation');
        if (isJudge && hackathon.status === 'IN_EVALUATION') workflowAction('Confirm evaluations', 'evaluations/confirm');
        if (isOrganizer && hackathon.status === 'CLOSED' && !winnerId) workflowAction('Declare winner', 'declare-winner');
        if (isOrganizer && hackathon.status === 'CLOSED' && winnerId && payoutStatus !== 'PAID') workflowAction('Retry / issue prize payout', 'prize-payout');
      }
      if (hackathon.status === 'CLOSED') await loadLeaderboard(id, content);
    } catch (error) {
      content.replaceChildren(make('div', 'form-error', error.message));
    }
  }

  async function loadLeaderboard(id, content) {
    append(content, make('h3', 'leaderboard-heading', 'Final leaderboard'));
    const holder = append(content, make('div', 'field-hint', 'Loading results…'));
    try {
      const rows = await api(`/api/hackathons/${encodeURIComponent(id)}/leaderboard`);
      holder.replaceChildren();
      if (!Array.isArray(rows) || rows.length === 0) { holder.textContent = 'No evaluated submissions are available for this event.'; return; }
      const table = append(holder, make('table', 'leaderboard'));
      const head = append(table, make('thead'));
      const header = append(head, make('tr'));
      ['#', 'Team', 'Score', 'Final'].forEach((label) => append(header, make('th', '', label)));
      const body = append(table, make('tbody'));
      rows.forEach((row) => {
        const tr = append(body, make('tr'));
        append(tr, make('td', '', row.rank));
        const teamCell = append(tr, make('td'));
        append(teamCell, make('span', '', row.teamName || `Team ${row.teamId}`));
        if (row.winner) append(teamCell, make('div', 'winner-label', '★ WINNER'));
        append(tr, make('td', '', `${row.submissionScore}/10`));
        append(tr, make('td', '', row.finalScore));
      });
    } catch (error) {
      holder.textContent = `Results could not be loaded: ${error.message}`;
    }
  }

  function updateAccountUI() {
    $('#account-actions').classList.toggle('hidden', Boolean(state.account));
    $('#account-signed-in').classList.toggle('hidden', !state.account);
    if (state.account) $('#signed-in-name').textContent = `${state.account.name || state.account.username} · ${state.account.role}`;
  }

  async function refreshProfile() {
    if (!state.token) { state.account = null; updateAccountUI(); return; }
    try {
      state.account = await api('/api/users/me');
    } catch {
      state.token = null; state.account = null; sessionStorage.removeItem('hackhubToken');
    }
    updateAccountUI();
  }

  function setAuthTab(tab) {
    const isLogin = tab === 'login';
    $('#login-form').classList.toggle('hidden', !isLogin);
    $('#register-form').classList.toggle('hidden', isLogin);
    $('#tab-login').classList.toggle('active', isLogin);
    $('#tab-register').classList.toggle('active', !isLogin);
    $('#tab-login').setAttribute('aria-selected', String(isLogin));
    $('#tab-register').setAttribute('aria-selected', String(!isLogin));
    $('#auth-title').replaceChildren(document.createTextNode(isLogin ? 'Welcome ' : 'Make it '), make('span', 'accent-text', isLogin ? 'back.' : 'happen.'));
    $('#auth-description').textContent = isLogin ? 'Sign in and pick up where your team left off.' : 'Create a participant account and find your next challenge.';
    $('#auth-error').classList.add('hidden');
  }

  function openAuth(tab) {
    setAuthTab(tab);
    $('#auth-dialog').showModal();
  }

  async function submitLogin(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submit = form.querySelector('button[type="submit"]');
    submit.disabled = true;
    $('#auth-error').classList.add('hidden');
    try {
      const data = Object.fromEntries(new FormData(form));
      const response = await api('/api/auth/login', { method: 'POST', body: JSON.stringify(data) });
      state.token = response.accessToken;
      sessionStorage.setItem('hackhubToken', state.token);
      await refreshProfile();
      $('#auth-dialog').close();
      form.reset();
      toast('You’re signed in. Welcome to HackHub.');
    } catch (error) {
      $('#auth-error').textContent = error.message;
      $('#auth-error').classList.remove('hidden');
    } finally { submit.disabled = false; }
  }

  async function submitRegister(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const submit = form.querySelector('button[type="submit"]');
    submit.disabled = true;
    $('#auth-error').classList.add('hidden');
    try {
      const data = Object.fromEntries(new FormData(form));
      const response = await api('/api/auth/register', { method: 'POST', body: JSON.stringify(data) });
      state.token = response.accessToken;
      sessionStorage.setItem('hackhubToken', state.token);
      await refreshProfile();
      $('#auth-dialog').close();
      form.reset();
      toast('Account created. Your next challenge is waiting.');
    } catch (error) {
      $('#auth-error').textContent = error.message;
      $('#auth-error').classList.remove('hidden');
    } finally { submit.disabled = false; }
  }

  $('#search-form').addEventListener('submit', (event) => { event.preventDefault(); state.page = 0; loadHackathons(); });
  ['filter-status', 'filter-online', 'filter-after', 'filter-before'].forEach((id) => $(`#${id}`).addEventListener('change', () => { state.page = 0; loadHackathons(); }));
  $('#clear-filters').addEventListener('click', () => {
    $('#filter-name').value = ''; $('#filter-status').value = ''; $('#filter-online').value = ''; $('#filter-after').value = ''; $('#filter-before').value = '';
    state.page = 0; loadHackathons();
  });
  $('#previous-page').addEventListener('click', () => { if (state.page > 0) { state.page--; loadHackathons(); $('#discover-title').scrollIntoView({ behavior: 'smooth', block: 'start' }); } });
  $('#next-page').addEventListener('click', () => { if (state.page + 1 < state.totalPages) { state.page++; loadHackathons(); $('#discover-title').scrollIntoView({ behavior: 'smooth', block: 'start' }); } });
  $('#open-login').addEventListener('click', () => openAuth('login'));
  $('#open-register').addEventListener('click', () => openAuth('register'));
  $('#hero-register').addEventListener('click', () => openAuth('register'));
  $('#bottom-register').addEventListener('click', () => openAuth('register'));
  $('#tab-login').addEventListener('click', () => setAuthTab('login'));
  $('#tab-register').addEventListener('click', () => setAuthTab('register'));
  $('#close-auth').addEventListener('click', () => $('#auth-dialog').close());
  $('#close-detail').addEventListener('click', () => $('#detail-dialog').close());
  $('#login-form').addEventListener('submit', submitLogin);
  $('#register-form').addEventListener('submit', submitRegister);
  $('#sign-out').addEventListener('click', () => {
    state.token = null; state.account = null; sessionStorage.removeItem('hackhubToken'); updateAccountUI(); toast('You have signed out.');
  });

  refreshProfile();
  loadHackathons();
})();
