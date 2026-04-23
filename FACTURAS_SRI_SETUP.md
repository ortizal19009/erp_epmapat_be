# Facturas SRI

Esta guía resume cómo se conecta este backend con el flujo de facturación electrónica.

## Microservicio relacionado

El servicio externo de SRI que ya tienes en:

`C:\Users\Alexi\Documents\PROYECTOS_EPMAPA-T\microservicesEpmapa-T\sri-files`

expone la lógica de envío, recepción y autorización de comprobantes.

## URL base usada por este backend

Por defecto el backend consume el microservicio en:

`http://localhost:8080`

La dirección se configura con:

`SRI_MICROSERVICE_BASE_URL`

Y se lee desde [.env](./.env) como:

`sri.microservice.base-url`

## Endpoints principales

Para facturas electrónicas, el flujo central usa:

- `POST /api/singsend/factura/xml`
- `POST /api/singsend/factura/string`
- `GET /api/singsend/autorizacion`
- `GET /api/singsend/generar-pdf`
- `POST /api/singsend/sendMail`

## Flujo general

1. Se genera el XML de la factura.
2. Se firma con el certificado configurado.
3. Se envía al SRI.
4. Se consulta la autorización.
5. Se arma el XML autorizado.
6. Se genera el PDF.
7. Se puede enviar por correo.
8. Se guarda el estado y la trazabilidad del documento.

## Cambio de puerto o servidor

Si el microservicio corre en otra máquina o puerto, ajusta:

```powershell
SRI_MICROSERVICE_BASE_URL=http://192.168.0.50:8080
```

o en local:

```powershell
SRI_MICROSERVICE_BASE_URL=http://localhost:8080
```

## Notas prácticas

- Mantén sincronizada la URL del microservicio en todos los entornos.
- Si el documento ya tiene número de autorización, debe tratarse como autorizado.
- El PDF solo debe generarse una vez que el comprobante esté autorizado.
