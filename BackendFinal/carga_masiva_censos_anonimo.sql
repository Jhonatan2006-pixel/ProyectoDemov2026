-- ============================================================
--  TaskFlow DB - Carga masiva de censos con PROCEDIMIENTO ANONIMO
--  (SQL Server, ejecutar en SQL Server Management Studio)
--
--  Es un bloque anonimo: se ejecuta una sola vez y no queda
--  guardado en la base de datos, a diferencia del procedimiento
--  almacenado de carga_masiva_censos.sql.
--
--  Inserta N censos de prueba con tipo_carga = 'MASIVA' para
--  distinguirlos de los capturados desde la aplicacion ('NORMAL').
--
--  Para cambiar la cantidad, modificar el valor de @cantidad.
-- ============================================================

USE TaskFlowDB;
GO

-- Cantidad de censos a insertar
DECLARE @cantidad INT = 5000;

BEGIN
    SET NOCOUNT ON;

    IF @cantidad IS NULL OR @cantidad <= 0 OR @cantidad > 1000000
    BEGIN
        RAISERROR('La cantidad de censos debe estar entre 1 y 1000000', 16, 1);
        RETURN;
    END

    -- 1. Catalogos de donde se toman los datos al azar
    DECLARE @nombres TABLE (
        n      INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL
    );

    INSERT INTO @nombres (nombre) VALUES
        ('Carlos'), ('Maria'), ('Jose'), ('Ana'), ('Luis'), ('Sofia'),
        ('Juan'), ('Gabriela'), ('Pedro'), ('Lucia'), ('Miguel'), ('Andrea'),
        ('Jorge'), ('Karla'), ('Mario'), ('Paola'), ('Diego'), ('Daniela'),
        ('Oscar'), ('Claudia'), ('Edgar'), ('Rosa'), ('Byron'), ('Silvia'),
        ('Erick'), ('Ingrid'), ('Hugo'), ('Mirna'), ('Kevin'), ('Brenda');

    DECLARE @apellidos TABLE (
        n        INT IDENTITY(1,1) PRIMARY KEY,
        apellido VARCHAR(100) NOT NULL
    );

    INSERT INTO @apellidos (apellido) VALUES
        ('Lopez'), ('Garcia'), ('Hernandez'), ('Morales'), ('Perez'), ('Gonzalez'),
        ('Ramirez'), ('Castillo'), ('Mendez'), ('Reyes'), ('Cruz'), ('Ortiz'),
        ('Flores'), ('Juarez'), ('Chavez'), ('Vasquez'), ('Estrada'), ('Barrios'),
        ('Aguilar'), ('Sandoval'), ('Monzon'), ('Cifuentes'), ('Tzul'), ('Ajpop'),
        ('Xicara'), ('Coc'), ('Caal'), ('Marroquin'), ('Solares'), ('De Leon');

    -- Ciudades de Guatemala: cada censo se ubica cerca de una de ellas
    DECLARE @ciudades TABLE (
        n        INT IDENTITY(1,1) PRIMARY KEY,
        ciudad   VARCHAR(50)   NOT NULL,
        latitud  DECIMAL(10,7) NOT NULL,
        longitud DECIMAL(10,7) NOT NULL
    );

    INSERT INTO @ciudades (ciudad, latitud, longitud) VALUES
        ('Ciudad de Guatemala',   14.6349, -90.5069),
        ('Mixco',                 14.6333, -90.6064),
        ('Villa Nueva',           14.5269, -90.5875),
        ('Antigua Guatemala',     14.5586, -90.7295),
        ('Chimaltenango',         14.6611, -90.8194),
        ('Escuintla',             14.3050, -90.7850),
        ('Quetzaltenango',        14.8347, -91.5181),
        ('Totonicapan',           14.9108, -91.3611),
        ('Solola',                14.7722, -91.1833),
        ('Santa Cruz del Quiche', 15.0306, -91.1489),
        ('Huehuetenango',         15.3197, -91.4708),
        ('San Marcos',            14.9653, -91.7944),
        ('Mazatenango',           14.5342, -91.5033),
        ('Retalhuleu',            14.5361, -91.6778),
        ('Coban',                 15.4703, -90.3711),
        ('Salama',                15.1033, -90.3189),
        ('Zacapa',                14.9722, -89.5306),
        ('Chiquimula',            14.8000, -89.5450),
        ('Jalapa',                14.6333, -89.9889),
        ('Jutiapa',               14.2917, -89.8958),
        ('Puerto Barrios',        15.7278, -88.5944),
        ('Flores',                16.9300, -89.8900);

    DECLARE @totalNombres   INT = (SELECT COUNT(*) FROM @nombres);
    DECLARE @totalApellidos INT = (SELECT COUNT(*) FROM @apellidos);
    DECLARE @totalCiudades  INT = (SELECT COUNT(*) FROM @ciudades);

    -- 2. Generar N filas con valores al azar.
    --    Se guardan primero en una tabla temporal para que cada
    --    fila conserve sus valores al unirla con los catalogos.
    WITH diez AS (
        SELECT d FROM (VALUES (0),(0),(0),(0),(0),(0),(0),(0),(0),(0)) AS t(d)
    ),
    filas AS (
        -- 10 x 10 x 10 x 10 x 10 x 10 = hasta 1,000,000 filas
        SELECT 1 AS fila
        FROM diez a
        CROSS JOIN diez b
        CROSS JOIN diez c
        CROSS JOIN diez d
        CROSS JOIN diez e
        CROSS JOIN diez f
    )
    SELECT TOP (@cantidad)
        ABS(CHECKSUM(NEWID()) % @totalNombres)   + 1 AS n_nombre,
        ABS(CHECKSUM(NEWID()) % @totalApellidos) + 1 AS n_apellido,
        ABS(CHECKSUM(NEWID()) % @totalCiudades)  + 1 AS n_ciudad,

        -- Entre 1 y 10 miembros, entre 0 y 5 televisiones
        ABS(CHECKSUM(NEWID()) % 10) + 1 AS total_miembros,
        ABS(CHECKSUM(NEWID()) % 6)      AS cantidad_televisiones,

        -- Desvio de hasta 0.05 grados (unos 5 km) alrededor de la ciudad
        (CHECKSUM(NEWID()) % 50001) / 1000000.0 AS desvio_latitud,
        (CHECKSUM(NEWID()) % 50001) / 1000000.0 AS desvio_longitud
    INTO #azar
    FROM filas;

    -- 3. Insertar los censos marcados como carga MASIVA
    INSERT INTO dbo.censos (
        nombre,
        apellido,
        total_miembros,
        cantidad_televisiones,
        latitud,
        longitud,
        tipo_carga,
        usuario_id,
        created_at
    )
    SELECT
        n.nombre,
        a.apellido,
        z.total_miembros,
        z.cantidad_televisiones,
        c.latitud  + z.desvio_latitud,
        c.longitud + z.desvio_longitud,
        'MASIVA',
        NULL,
        SYSDATETIME()
    FROM #azar z
    INNER JOIN @nombres   n ON n.n = z.n_nombre
    INNER JOIN @apellidos a ON a.n = z.n_apellido
    INNER JOIN @ciudades  c ON c.n = z.n_ciudad;

    DECLARE @insertados INT = @@ROWCOUNT;

    DROP TABLE #azar;

    PRINT CONCAT('Censos masivos insertados: ', @insertados);
END
GO

-- Verificar cuantos censos hay de cada tipo
SELECT tipo_carga, COUNT(*) AS total_censos
FROM dbo.censos
GROUP BY tipo_carga;
GO
