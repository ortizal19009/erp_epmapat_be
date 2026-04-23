# Retenciones SRI

Este backend usa el microservicio SRI externo que tienes en:

`C:\Users\Alexi\Documents\PROYECTOS_EPMAPA-T\microservicesEpmapa-T\sri-files`

## URL del microservicio

Por defecto el backend apunta a:

`http://localhost:8080`

La URL se controla con la variable:

`SRI_MICROSERVICE_BASE_URL`

En este proyecto esa variable se lee desde [.env](./.env) y luego se expone como:

`sri.microservice.base-url`

## Endpoints usados por retenciones

El flujo de retenciones llama al microservicio SRI en:

- `POST /api/singsend/retencion/string`
- `GET /api/singsend/retencion/download`
- `GET /api/singsend/autorizacion`

## Flujo del backend actual

1. Genera el XML de retención.
2. Lo envía al microservicio SRI.
3. Espera autorización.
4. Guarda el número de autorización y la fecha.
5. Envía el PDF por correo.
6. Registra el envío en `correos_enviados`.

## Si cambias el puerto

Si levantas `sri-files` en otro puerto u host, solo cambia la variable:

```powershell
SRI_MICROSERVICE_BASE_URL=http://192.168.0.50:8080
```

o en local:

```powershell
SRI_MICROSERVICE_BASE_URL=http://localhost:8080
```

## Observaciones

- El PDF de retención solo se genera cuando la retención está autorizada.
- Las retenciones con número de autorización se consideran `AUTORIZADA`.
- El historial de correos enviados queda disponible para consulta administrativa.
