-- La dosis del dominio (Value Object Dosage) se expresa en GRAMOS.
-- Se renombra la columna en vez de editar V1: Flyway no permite modificar
-- migraciones ya aplicadas (el checksum cambiaría y la app no arrancaría).
ALTER TABLE supplement_doses RENAME COLUMN amount_mg TO amount_grams;
