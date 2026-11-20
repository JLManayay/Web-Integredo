/* FitSync — utilidades compartidas por todas las pantallas. */
const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
const DAY_ES = {MONDAY:'Lunes',TUESDAY:'Martes',WEDNESDAY:'Miércoles',THURSDAY:'Jueves',FRIDAY:'Viernes',SATURDAY:'Sábado',SUNDAY:'Domingo'};
const SPLIT_ES = {PUSH_PULL_LEGS:'Push / Pull / Legs',UPPER_LOWER:'Torso / Pierna',FULL_BODY:'Cuerpo completo',BRO_SPLIT:'Un músculo por día'};

const TOKEN_KEY = 'fitsync.token';
const VIEW_KEY = 'fitsync.viewAs';
let ME = null;    // cuenta con la sesión abierta
let VIEW = null;  // atleta que el entrenador está revisando (o null)

function store(key, value) {
  try { value === null ? localStorage.removeItem(key) : localStorage.setItem(key, value); } catch (e) { /* sin almacenamiento */ }
}
function read(key) {
  try { return localStorage.getItem(key); } catch (e) { return null; }
}

/** Llama a la API REST con el token de la sesión. Lanza Error con el mensaje del servidor. */
async function api(path, method = 'GET', body) {
  const headers = {'Content-Type': 'application/json'};
  const token = read(TOKEN_KEY);
  if (token) headers.Authorization = 'Bearer ' + token;
  const res = await fetch('/api' + path, {method, headers, body: body ? JSON.stringify(body) : undefined});
  let data = null;
  try { data = await res.json(); } catch (e) { /* respuesta sin cuerpo */ }
  if (res.status === 401 && !path.startsWith('/auth/')) {
    store(TOKEN_KEY, null); store(VIEW_KEY, null);
    location.href = 'login.html';
    throw new Error('Inicia sesión para continuar.');
  }
  if (!res.ok) {
    const err = new Error((data && data.message) || ('Error ' + res.status));
    err.status = res.status;
    throw err;
  }
  return data;
}

const $ = (s, el = document) => el.querySelector(s);
function esc(t) { const d = document.createElement('div'); d.textContent = t == null ? '' : t; return d.innerHTML; }

function toast(msg) {
  const t = $('#toast'); if (!t) return;
  t.textContent = msg; t.classList.add('show');
  clearTimeout(toast.h); toast.h = setTimeout(() => t.classList.remove('show'), 3200);
}

/** Muestra un error de regla de negocio dentro del formulario (útil para la demo). */
function showRule(box, msg) {
  // Los mensajes del dominio nombran los días en inglés (MONDAY...): se traducen para mostrarlos.
  const es = msg.replace(/MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY/g, d => DAY_ES[d].toLowerCase());
  box.innerHTML = '<b>Regla de negocio</b>' + esc(es); box.classList.add('show');
}
function clearRule(box) { box.classList.remove('show'); }

/** De quién son los datos que se muestran: los propios o los del atleta que revisa el entrenador. */
const ownerId = () => (VIEW ? VIEW.id : ME.id);
const ownerQS = (sep = '?') => (VIEW ? sep + 'userId=' + VIEW.id : '');
const isCoach = () => ME && ME.role === 'ENTRENADOR';
const isPro = () => ME && ME.plan === 'PRO';

function setView(athlete) { store(VIEW_KEY, athlete ? JSON.stringify({id: athlete.id, fullName: athlete.fullName}) : null); }

async function logout() {
  try { await api('/auth/logout', 'POST'); } catch (e) { /* la sesión igual se cierra en el navegador */ }
  store(TOKEN_KEY, null); store(VIEW_KEY, null);
  location.href = 'login.html';
}

/** Marca blanca: el atleta ve el nombre y el color de su entrenador. */
function applyBrand() {
  const brand = (ME.coach && ME.coach.brand) || (isCoach() ? ME.brand : null);
  if (brand) document.documentElement.style.setProperty('--coral', brand.color);
  return brand;
}

/** Comprueba la sesión, pinta la barra superior y deja marcada la pestaña activa. */
async function shell(active) {
  document.body.insertAdjacentHTML('afterbegin', '<div id="toast" role="status"></div>');
  if (!read(TOKEN_KEY)) { location.href = 'login.html'; throw new Error('Sin sesión'); }
  ME = await api('/me');
  try { VIEW = isCoach() && read(VIEW_KEY) ? JSON.parse(read(VIEW_KEY)) : null; } catch (e) { VIEW = null; }
  if (active === 'atletas' || active === 'plan') { VIEW = null; setView(null); }
  const brand = applyBrand();

  const tabs = [['index.html','Semana','semana'],['entrenamiento.html','Entrenamiento','entreno'],
    ['suplementos.html','Suplementación','supl'],['progreso.html','Progreso','progreso'],['plan.html','Mi plan','plan']];
  if (isCoach()) tabs.push(['atletas.html','Atletas','atletas']);

  const sub = brand ? '<small class="by">' + (ME.coach ? 'con ' : '') + esc(brand.name) + '</small>' : '';
  document.body.insertAdjacentHTML('afterbegin',
    '<header class="top"><div class="wrap"><a class="brand" href="index.html"><i></i>FitSync' + sub + '</a>' +
    '<nav class="tabs">' + tabs.map(t => '<a href="' + t[0] + '"' + (t[2] === active ? ' class="active"' : '') + '>' + t[1] + '</a>').join('') + '</nav>' +
    '<span class="who">' + esc(ME.fullName) + ' <span class="chip ' + (isPro() ? 'pro' : '') + '">' + (isPro() ? 'Pro' : 'Gratis') + '</span>' +
    ' <button class="link" id="btnLogout">Salir</button></span></div></header>' +
    (VIEW ? '<div class="viewing"><div class="wrap">Estás viendo los datos de <b>' + esc(VIEW.fullName) +
      '</b> <button class="link" id="btnBackToMe">Volver a mis datos</button></div></div>' : ''));
  $('#btnLogout').onclick = logout;
  if (VIEW) $('#btnBackToMe').onclick = () => { setView(null); location.reload(); };
}

/** Ejecuta la carga de una pantalla tras validar la sesión. */
function start(active, load) {
  shell(active).then(load).catch(e => {
    if (e.status === 403 && VIEW) { setView(null); toast(e.message); setTimeout(() => location.reload(), 1500); return; }
    toast(e.message);
  });
}

const listPrograms = () => api('/workout-programs' + ownerQS());
const listPlans = () => api('/supplement-plans' + ownerQS());
const shortId = id => id.slice(0, 4).toUpperCase();
function planLabel(plan, programs) {
  const p = programs.find(x => x.id === plan.linkedProgramId);
  return 'Plan ' + shortId(plan.id) + (p ? ' · ' + p.name : ' · sin vincular');
}

/** Programa y plan elegidos, compartidos entre Semana y Progreso. */
const SEL_KEY = () => 'fitsync.sel.' + ownerId();
function saveSelection(progId, planId) { store(SEL_KEY(), JSON.stringify({progId, planId})); }
function loadSelection() { try { return JSON.parse(read(SEL_KEY())) || {}; } catch (e) { return {}; } }

function pickers(programs, plans, progId, planId) {
  return '<div class="picks"><div><label for="selProg">Programa de entrenamiento</label><select id="selProg">' +
    programs.map(p => '<option value="' + p.id + '"' + (p.id === progId ? ' selected' : '') + '>' + esc(p.name) + '</option>').join('') + '</select></div>' +
    '<div><label for="selPlan">Plan de suplementación</label><select id="selPlan"><option value="">Ninguno</option>' +
    plans.map(p => '<option value="' + p.id + '"' + (p.id === planId ? ' selected' : '') + '>' + esc(planLabel(p, programs)) + '</option>').join('') + '</select></div></div>';
}
function defaultPlanFor(plans, progId) {
  const linked = plans.find(p => p.linkedProgramId === progId);
  return linked ? linked.id : (plans.length ? plans[0].id : null);
}
