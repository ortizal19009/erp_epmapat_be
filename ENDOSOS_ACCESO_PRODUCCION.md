# Acceso a Endosos en produccion

El usuario activo ID 1 recibe CONSULTAR, SOLICITAR, AUTORIZAR, EJECUTAR,
REVERTIR y REPORTES sin filas en nc_endoso_permiso. No se omiten las reglas
que impiden autorizar una solicitud propia ni las validaciones de saldo.
Los demas usuarios conservan exclusivamente los permisos asignados.
Ambas rutas Angular siguen requiriendo acceso a la ventana ntacredito.

## Despliegue

1. Desplegar el backend actualizado.
2. La migracion src/main/resources/sql/2026-10-19_nc_endosos.sql debe estar
   aplicada antes de operar. No volver a ejecutarla si ya esta aplicada.
3. El servicio de Endosos se habilita por defecto si no hay configuracion
   explicita. ERP_NC_ENDOSOS_ENABLED=false o erp.nc-endosos.enabled=false
   lo deshabilitan para todos. El esquema se comprueba antes de dar acceso.
4. Iniciar sesion WEB nuevamente y abrir nc-endosos-listado o nc-endosos?nota=9.

## Asignar permisos a un operador

Ademas del acceso al modulo WEB correspondiente y a la ventana ntacredito,
requiere permisos especificos en nc_endoso_permiso. Ejemplo de SQL para
revision (sustituir :usuario_id por el ID real; ejecutar con parametro):

```sql
INSERT INTO nc_endoso_permiso (usuario_id, permiso)
SELECT u.idusuario, p.permiso
FROM usuarios u
CROSS JOIN (VALUES ('CONSULTAR'), ('SOLICITAR')) AS p(permiso)
WHERE u.idusuario = :usuario_id AND u.estado = true
ON CONFLICT (usuario_id, permiso) DO NOTHING;
```

CONSULTAR permite listado/detalle; SOLICITAR crear y presentar;
AUTORIZAR aprobar/rechazar; EJECUTAR ejecutar; REVERTIR revertir;
REPORTES exportar. Otorgar solo las acciones necesarias.

## Verificacion

GET /api/nc-endosos/permisos con JWT valido devuelve las seis acciones
para el administrador activo y solo las asignadas para otros usuarios.
Un usuario sin sesion recibe 401; uno inactivo, 403. Una accion sin permiso
recibe 403. Si falta el esquema o hay deshabilitacion explicita, devuelve 503
con una causa concreta. No se cambiaron datos ni configuracion del servidor
192.168.0.88 desde este trabajo local.
