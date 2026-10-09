-- Solo lectura. Ajustar el periodo para la jornada de prueba.
-- Fechas comparadas tal como estan almacenadas (timestamp sin zona).
WITH periodo AS (SELECT DATE '2026-10-08' AS desde, DATE '2026-10-09' AS hasta)
SELECT COUNT(*) AS lecturas_capturadas,
       COUNT(*) FILTER (WHERE l.tracking_session_id IS NULL) AS sin_sesion,
       COUNT(*) FILTER (WHERE l.tracking_session_id IS NOT NULL AND s.id IS NULL) AS sesion_inexistente,
       COUNT(*) FILTER (WHERE l.reading_latitude BETWEEN -90 AND 90
                              AND l.reading_longitude BETWEEN -180 AND 180
                              AND l.reading_accuracy >= 0
                              AND l.reading_accuracy < 'Infinity'::double precision) AS con_gps_valido,
       COUNT(*) FILTER (WHERE s.id IS NOT NULL AND l.usuariolectura = s.reader_id) AS con_lector_coincidente,
       COUNT(*) FILTER (WHERE s.id IS NOT NULL AND EXISTS (
           SELECT 1 FROM public.tracking_points p WHERE p.tracking_session_id = s.id
       )) AS con_recorrido_sincronizado
FROM public.lecturas l
CROSS JOIN periodo
LEFT JOIN public.tracking_sessions s ON s.id = l.tracking_session_id
WHERE l.reading_captured_at >= periodo.desde AND l.reading_captured_at < periodo.hasta;

SELECT work_date, COUNT(*) AS sesiones,
       SUM(total_points) AS puntos_contabilizados,
       SUM(total_readings) AS lecturas_contabilizadas
FROM public.tracking_sessions
WHERE work_date = DATE '2026-10-08'
GROUP BY work_date;

SELECT COUNT(*) AS puntos_persistidos,
       COUNT(*) FILTER (WHERE latitude NOT BETWEEN -90 AND 90
                              OR longitude NOT BETWEEN -180 AND 180
                              OR accuracy IS NULL OR accuracy < 0
                              OR accuracy >= 'Infinity'::double precision) AS puntos_gps_invalidos
FROM public.tracking_points
WHERE captured_at >= TIMESTAMP '2026-10-08 00:00:00'
  AND captured_at < TIMESTAMP '2026-10-09 00:00:00';
