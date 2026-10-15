-- =========================================================
-- Gestión de Turnos – script para MySQL
-- =========================================================
CREATE DATABASE IF NOT EXISTS turnos;
USE turnos;

DROP TABLE IF EXISTS turno;

CREATE TABLE turno (
    numero          INT PRIMARY KEY,
    estado          VARCHAR(20) NOT NULL,
    fecha_creacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO turno (numero, estado) VALUES (1, 'PENDIENTE');
INSERT INTO turno (numero, estado) VALUES (2, 'PENDIENTE');
INSERT INTO turno (numero, estado) VALUES (3, 'ATENDIDO');

SELECT * FROM turno;
