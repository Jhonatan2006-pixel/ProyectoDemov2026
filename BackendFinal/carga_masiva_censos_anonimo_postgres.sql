-- ============================================================
--  TaskFlow DB - Carga masiva de censos con PROCEDIMIENTO ANONIMO
--  (PostgreSQL / PL/pgSQL, servidor de AWS)
--
--  Es un bloque anonimo (DO): se ejecuta una sola vez y no queda
--  guardado en la base de datos, a diferencia del procedimiento
--  almacenado de carga_masiva_censos_postgres.sql.
--
--  Inserta N censos de prueba con tipo_carga = 'MASIVA' para
--  distinguirlos de los capturados desde la aplicacion ('NORMAL').
--
--  Para cambiar la cantidad, modificar el valor de p_cantidad.
--  Ejecutar en el servidor:
--    sudo -u postgres psql -d taskflowdb < carga_masiva_censos_anonimo_postgres.sql
-- ============================================================

DO $$
DECLARE
    -- Cantidad de censos a insertar
    p_cantidad INTEGER := 5000;

    -- 1. Catalogos de donde se toman los datos al azar
    v_nombres TEXT[] := ARRAY[
        'Carlos', 'Maria', 'Jose', 'Ana', 'Luis', 'Sofia',
        'Juan', 'Gabriela', 'Pedro', 'Lucia', 'Miguel', 'Andrea',
        'Jorge', 'Karla', 'Mario', 'Paola', 'Diego', 'Daniela',
        'Oscar', 'Claudia', 'Edgar', 'Rosa', 'Byron', 'Silvia',
        'Erick', 'Ingrid', 'Hugo', 'Mirna', 'Kevin', 'Brenda'
    ];

    v_apellidos TEXT[] := ARRAY[
        'Lopez', 'Garcia', 'Hernandez', 'Morales', 'Perez', 'Gonzalez',
        'Ramirez', 'Castillo', 'Mendez', 'Reyes', 'Cruz', 'Ortiz',
        'Flores', 'Juarez', 'Chavez', 'Vasquez', 'Estrada', 'Barrios',
        'Aguilar', 'Sandoval', 'Monzon', 'Cifuentes', 'Tzul', 'Ajpop',
        'Xicara', 'Coc', 'Caal', 'Marroquin', 'Solares', 'De Leon'
    ];

    -- Ciudades de Guatemala: cada censo se ubica cerca de una de ellas.
    -- La posicion en v_latitudes y v_longitudes corresponde a la misma ciudad:
    --   Ciudad de Guatemala, Mixco, Villa Nueva, Antigua Guatemala,
    --   Chimaltenango, Escuintla, Quetzaltenango, Totonicapan, Solola,
    --   Santa Cruz del Quiche, Huehuetenango, San Marcos, Mazatenango,
    --   Retalhuleu, Coban, Salama, Zacapa, Chiquimula, Jalapa, Jutiapa,
    --   Puerto Barrios, Flores
    v_latitudes NUMERIC[] := ARRAY[
        14.6349, 14.6333, 14.5269, 14.5586,
        14.6611, 14.3050, 14.8347, 14.9108, 14.7722,
        15.0306, 15.3197, 14.9653, 14.5342,
        14.5361, 15.4703, 15.1033, 14.9722, 14.8000, 14.6333, 14.2917,
        15.7278, 16.9300
    ];

    v_longitudes NUMERIC[] := ARRAY[
        -90.5069, -90.6064, -90.5875, -90.7295,
        -90.8194, -90.7850, -91.5181, -91.3611, -91.1833,
        -91.1489, -91.4708, -91.7944, -91.5033,
        -91.6778, -90.3711, -90.3189, -89.5306, -89.5450, -89.9889, -89.8958,
        -88.5944, -89.8900
    ];

    v_insertados INTEGER;
BEGIN
    IF p_cantidad IS NULL OR p_cantidad <= 0 OR p_cantidad > 1000000 THEN
        RAISE EXCEPTION 'La cantidad de censos debe estar entre 1 y 1000000';
    END IF;

    -- 2. Generar N filas con valores al azar.
    --    MATERIALIZED hace que cada fila conserve sus valores, para que
    --    la latitud y la longitud salgan de la misma ciudad.
    WITH azar AS MATERIALIZED (
        SELECT
            1 + floor(random() * array_length(v_nombres, 1))::INTEGER   AS n_nombre,
            1 + floor(random() * array_length(v_apellidos, 1))::INTEGER AS n_apellido,
            1 + floor(random() * array_length(v_latitudes, 1))::INTEGER AS n_ciudad,

            -- Entre 1 y 10 miembros, entre 0 y 5 televisiones
            1 + floor(random() * 10)::INTEGER AS total_miembros,
            floor(random() * 6)::INTEGER      AS cantidad_televisiones,

            -- Desvio de hasta 0.05 grados (unos 5 km) alrededor de la ciudad
            (random() * 0.1 - 0.05)::NUMERIC(10,7) AS desvio_latitud,
            (random() * 0.1 - 0.05)::NUMERIC(10,7) AS desvio_longitud
        FROM generate_series(1, p_cantidad)
    )
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
        v_nombres[z.n_nombre],
        v_apellidos[z.n_apellido],
        z.total_miembros,
        z.cantidad_televisiones,
        v_latitudes[z.n_ciudad]  + z.desvio_latitud,
        v_longitudes[z.n_ciudad] + z.desvio_longitud,
        'MASIVA',
        NULL,
        now()
    FROM azar z;

    GET DIAGNOSTICS v_insertados = ROW_COUNT;

    RAISE NOTICE 'Censos masivos insertados: %', v_insertados;
END;
$$;

-- Verificar cuantos censos hay de cada tipo
SELECT tipo_carga, COUNT(*) AS total_censos
FROM dbo.censos
GROUP BY tipo_carga;
