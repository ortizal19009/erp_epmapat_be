# Nextcloud Storage

Las fotos de `lecturas` y `abonados` ya usan `StorageService` en el backend. Eso significa que, cuando `storage.type=nextcloud`, los uploads se guardan en Nextcloud sin cambiar el flujo de la app Android.
## Endpoints que ya suben fotos

- `POST /lecturas/{idlectura}/foto`
- `POST /abonados/{idabonado}/fotos`
- `POST /api/storage/upload`
- `POST /api/storage/upload/abonado`

## Configuracion recomendada

Usa variables de entorno en el servidor backend:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:STORAGE_TYPE="nextcloud"
$env:NEXTCLOUD_BASE_URL="https://192.168.0.52/nextcloud"
$env:NEXTCLOUD_USERNAME="admin"
$env:NEXTCLOUD_APP_TOKEN="TU_TOKEN_O_PASSWORD"
$env:NEXTCLOUD_BASE_FOLDER="/EPMAPAT-PHOTOS"
```

O en Linux:

```bash
export SPRING_PROFILES_ACTIVE=local
export STORAGE_TYPE=nextcloud
export NEXTCLOUD_BASE_URL=https://192.168.0.52/nextcloud
export NEXTCLOUD_USERNAME=admin
export NEXTCLOUD_APP_TOKEN=TU_TOKEN_O_PASSWORD
export NEXTCLOUD_BASE_FOLDER=/EPMAPAT-PHOTOS
```

## Verificacion rapida

1. Levanta el backend con perfil `local` o `prod`, pero con `STORAGE_TYPE=nextcloud`.
2. Consulta `GET /api/storage/info`.
3. Debe responder algo como:

```json
{
  "storageType": "nextcloud",
  "implementation": "NextcloudStorageService",
  "nextcloudBaseUrl": "https://192.168.0.52/nextcloud",
  "nextcloudUsername": "admin",
  "nextcloudBaseFolder": "/EPMAPAT-PHOTOS"
}
```

4. Sube una foto de prueba con `POST /api/storage/upload`.
5. Verifica en Nextcloud que aparezca dentro de `/EPMAPAT-PHOTOS/...`.

## Scripts incluidos

Para arrancar el backend con Nextcloud:

```powershell
.\run-backend-nextcloud.ps1 `
  -NextcloudUsername "admin" `
  -NextcloudAppToken "TU_TOKEN"
```

Ese script ahora arranca por defecto con:

- `SPRING_PROFILES_ACTIVE=local`
- `STORAGE_TYPE=nextcloud`

Así puedes probar en tu entorno local usando Nextcloud real para las fotos.

Para probar una subida real:

```powershell
.\test-nextcloud-upload.ps1 `
  -BackendBaseUrl "http://localhost:9080" `
  -FilePath "C:\ruta\foto.jpg" `
  -Folder "lecturas/prueba"
```

## Notas

- Para Nextcloud es mejor usar un `app password` que la clave principal del usuario.
- El backend guarda en la base solo la ruta relativa, por ejemplo `EPMAPAT-PHOTOS/LECTURAS/R001/uuid_archivo.jpg`.
- Si `storage.type=local`, el mismo flujo sigue funcionando, pero almacenando en disco local.
- Si quieres forzar Nextcloud sin cambiar de perfil, usa `STORAGE_TYPE=nextcloud`.
