-- ============================================================
--  Tabla sesiones_biometricas (SQL Server, base local TaskFlowDB)
--  Ejecutar en SQL Server Management Studio.
--
--  Cada intento de login biometrico: su UUID, el resultado de
--  Rekognition y si termino en EXITO (onSuccess) o ERROR (onError).
--    tipo ROSTRO = selfie comparada con la foto de perfil
--    tipo VIDA   = prueba de vida (Face Liveness) + comparacion
--  En el servidor de AWS esta tabla se crea con database_postgres.sql
-- ============================================================

USE TaskFlowDB;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'sesiones_biometricas' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.sesiones_biometricas (
        id          INT IDENTITY(1,1)  NOT NULL,
        session_id  VARCHAR(64)        NOT NULL,
        usuario_id  INT                NOT NULL,
        tipo        VARCHAR(10)        NOT NULL,
        estado      VARCHAR(20)        NOT NULL DEFAULT 'CREADA',
        confianza   DECIMAL(5,2)           NULL,
        similitud   DECIMAL(5,2)           NULL,
        resultado   VARCHAR(10)            NULL,
        mensaje     VARCHAR(255)           NULL,
        created_at  DATETIMEOFFSET     NOT NULL DEFAULT SYSDATETIMEOFFSET(),
        updated_at  DATETIMEOFFSET         NULL,
        CONSTRAINT PK_sesiones_biometricas   PRIMARY KEY (id),
        CONSTRAINT UQ_sesiones_session_id    UNIQUE (session_id),
        CONSTRAINT FK_sesiones_usuario       FOREIGN KEY (usuario_id)
            REFERENCES dbo.usuarios(id),
        CONSTRAINT CK_sesiones_tipo          CHECK (tipo IN ('ROSTRO', 'VIDA')),
        CONSTRAINT CK_sesiones_resultado     CHECK (resultado IN ('EXITO', 'ERROR'))
    );
END
GO

-- Ultimos intentos de login biometrico
SELECT TOP 20 s.id, s.session_id, u.email, s.tipo, s.estado,
       s.confianza, s.similitud, s.resultado, s.mensaje, s.created_at
FROM dbo.sesiones_biometricas s
JOIN dbo.usuarios u ON u.id = s.usuario_id
ORDER BY s.id DESC;
GO
