/* FitSync — utilidades compartidas por todas las pantallas. */
const USER_ID = '11111111-1111-1111-1111-111111111111'; // usuario demo (migración V2)
const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
const DAY_ES = {MONDAY:'Lunes',TUESDAY:'Martes',WEDNESDAY:'Miércoles',THURSDAY:'Jueves',FRIDAY:'Viernes',SATURDAY:'Sábado',SUNDAY:'Domingo'};
const SPLIT_ES = {PUSH_PULL_LEGS:'Push / Pull / Legs',UPPER_LOWER:'Torso / Pierna',FULL_BODY:'Cuerpo completo',BRO_SPLIT:'Un músculo por día'};

/** Llama a la API REST del backend (mismo origen, sin CORS). Lanza Error con el mensaje del servidor. */
async function api(path, method = 'GET', body) {
  const res = await fetch('/api' + path, {
    method,
    headers: {'Content-Type': 'application/json'},
    body: body ? JSON.stringify(body) : undefined
  });
  let data = null;
  try { data = await res.json(); } catch (e) { /* respuesta sin cuerpo */ }
  if (!res.ok) throw new Error((data && data.message) || ('Error ' + res.status));
  return data;
}

const $ = (s, el = document) => el.querySelector(s);
function esc(t) { const d = document.createElement('div'); d.textContent = t; return d.innerHTML; }

function toast(msg) {
  const t = $('#toast'); t.textContent = msg; t.classList.add('show');
  clearTimeout(toast.h); toast.h = setTimeout(() => t.classList.remove('show'), 2600);
}

/** Muestra un error de regla de negocio dentro del formulario (útil para la demo). */
function showRule(box, msg) {
  // Los mensajes del dominio nombran los días en inglés (MONDAY...): se traducen para mostrarlos.
  const es = msg.replace(/MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY/g, d => DAY_ES[d].toLowerCase());
  box.innerHTML = '<b>Regla de negocio</b>' + esc(es); box.classList.add('show');
}
function clearRule(box) { box.classList.remove('show'); }

/** Pinta la barra superior y deja marcada la pestaña activa. */
function shell(active) {
  const tabs = [['index.html','Semana','semana'],['entrenamiento.html','Entrenamiento','entreno'],['suplementos.html','Suplementación','supl']];
  document.body.insertAdjacentHTML('afterbegin',
    '<header class="top"><div class="wrap"><a class="brand" href="index.html"><i></i>FitSync</a>' +
    '<nav class="tabs">' + tabs.map(t => '<a href="' + t[0] + '"' + (t[2] === active ? ' class="active"' : '') + '>' + t[1] + '</a>').join('') + '</nav>' +
    '<span class="who">Usuario Demo</span></div></header><div id="toast" role="status"></div>');
}

const listPrograms = () => api('/workout-programs?userId=' + USER_ID);
const listPlans = () => api('/supplement-plans?userId=' + USER_ID);
const shortId = id => id.slice(0, 4).toUpperCase();
function planLabel(plan, programs) {
  const p = programs.find(x => x.id === plan.linkedProgramId);
  return 'Plan ' + shortId(plan.id) + (p ? ' · ' + p.name : ' · sin vincular');
}
