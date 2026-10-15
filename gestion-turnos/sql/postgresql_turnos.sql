-- =========================================================
-- Gestión de Turnos – script para PostgreSQL
-- Antes de ejecutarlo: crear la base de datos "turnos"
--   CREATE DATABASE turnos;
-- y conectarse a ella (en psql: \c turnos).
-- =========================================================
DROP TABLE IF EXISTS turno;

CREATE TABLE turno (
    numero          INTEGER PRIMARY KEY,
    estado          VARCHAR(20) NOT NULL,
    fecha_creacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO turno (numero, estado) VALUES (1, 'PENDIENTE');
INSERT INTO turno (numero, estado) VALUES (2, 'PENDIENTE');
INSERT INTO turno (numero, estado) VALUES (3, 'ATENDIDO');

SELECT * FROM turno;
