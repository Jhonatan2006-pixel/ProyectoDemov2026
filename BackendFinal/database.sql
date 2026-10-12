-- ============================================================
--  TaskFlow DB - Script de creacion de base de datos
--  Ejecutar en SQL Server Management Studio
-- ============================================================

-- 1. Crear la base de datos
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'TaskFlowDB')
BEGIN
    CREATE DATABASE TaskFlowDB;
END
GO

USE TaskFlowDB;
GO

-- 2. Crear tabla proyectos
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'proyectos' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.proyectos (
        id          INT IDENTITY(1,1)  NOT NULL,
        nombre      VARCHAR(100)       NOT NULL,
        descripcion VARCHAR(255)           NULL,
        CONSTRAINT PK_proyectos PRIMARY KEY (id)
    );
END
GO

-- 3. Crear tabla tareas
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'tareas' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.tareas (
        id          INT IDENTITY(1,1)  NOT NULL,
        titulo      VARCHAR(100)       NOT NULL,
        descripcion VARCHAR(255)           NULL,
        estado      VARCHAR(20)        NOT NULL DEFAULT 'pendiente',
        proyecto_id INT                NOT NULL,
        created_at  DATETIME               NULL,
        updated_at  DATETIME               NULL,
        CONSTRAINT PK_tareas       PRIMARY KEY (id),
        CONSTRAINT FK_tareas_proy  FOREIGN KEY (proyecto_id)
            REFERENCES dbo.proyectos(id)
    );
END
GO

-- ============================================================
--  Datos de prueba (opcional, comentar si no se necesitan)
-- ============================================================

INSERT INTO dbo.proyectos (nombre, descripcion) VALUES
    ('Rediseno de sitio web',    'Modernizar la pagina principal de la empresa'),
    ('App movil de clientes',    'Aplicacion para que los clientes hagan pedidos'),
    ('Sistema de facturacion',   'Automatizar la generacion de facturas mensuales');
GO

INSERT INTO dbo.tareas (titulo, descripcion, estado, proyecto_id) VALUES
    ('Diseno de wireframes',     'Crear los mockups de todas las vistas', 'completada',   1),
    ('Implementar navbar',       'Barra de navegacion responsive',        'en progreso',  1),
    ('Configurar base de datos', 'Crear tablas y relaciones en SQL',      'pendiente',    1),
    ('Diseno de pantalla login', 'Pantalla de inicio de sesion movil',    'completada',   2),
    ('API de autenticacion',     'Endpoints para registro y login',       'en progreso',  2),
    ('Modulo de reportes',       'Reportes en PDF por mes',               'pendiente',    3);
GO

-- Verificar datos
SELECT p.id, p.nombre, COUNT(t.id) AS total_tareas
FROM dbo.proyectos p
LEFT JOIN dbo.tareas t ON t.proyecto_id = p.id
GROUP BY p.id, p.nombre;
GO
