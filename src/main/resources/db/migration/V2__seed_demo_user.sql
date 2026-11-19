-- Usuario de demostración para poder probar la API sin construir aún
-- el módulo de Identidad y Perfiles. UUID fijo para usarlo en los ejemplos.
INSERT INTO users (id, full_name, email, password_hash, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'Usuario Demo', 'demo@fitsync.com', 'no-aplica-en-este-avance', 'ATLETA');
