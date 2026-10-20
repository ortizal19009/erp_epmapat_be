# Arranque local con clases verificadas

## Error corregido el 20 de octubre de 2026

El arranque fallaba con `ClassFormatError: Invalid method Code length 0` al cargar
`EmailAccount`. La comprobación de `target/classes` encontró 499 fallos de carga,
incluidas referencias a otras entidades y DTO. El código fuente de `EmailAccount`
no necesitó cambios.

Se recompiló con Maven en `target/local-runtime/classes`, verificando las 1.139
clases sin inicializar la aplicación ni conectarse a la base de datos. Después se
reparó `target/classes` con esa compilación y se verificó también sin errores.
Las clases anteriores se conservaron en `target/classes-invalidas-*`.

Durante la reparación, el compilador automático del editor volvió a escribir
clases inválidas en la carpeta habitual. Se configuró
`java.autobuild.enabled = false` en `.vscode/settings.json` para este proyecto;
la compilación pasa a realizarse con Maven. La carpeta reparada se verificó de
nuevo después de ese cambio. El arranque aislado evita compartir la salida con
el editor incluso si otra instancia sigue compilando.

## Comando recomendado

Desde la raíz del backend, tanto en PowerShell como en Git Bash:

```text
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-local.ps1
```

El script carga `.env.local` como valores literales, selecciona el perfil `local`,
compila en una salida separada de la carpeta habitual del IDE, verifica las clases
y después ejecuta Spring Boot. No imprime contraseñas ni modifica el archivo de
configuración local.

Para compilar y verificar sin arrancar el backend:

```text
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-local.ps1 -VerifyOnly
```

Para reparar además la carpeta habitual de clases:

```text
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-local.ps1 -VerifyOnly -RepairDefaultClasses
```

El perfil Maven usado es `local-runtime`; los logs de compilación quedan en
`target/local-runtime/compilacion.log`. Los respaldos de la validación de endosos
en `target/nc-uat-20261020/` se conservan: el script no ejecuta `mvn clean` ni borra
el directorio `target` completo.

Validación realizada: compilación Maven aprobada y carga de las 1.139 clases sin
errores, tanto en la salida aislada como en la carpeta reparada. El proceso de
Spring Boot no se inició automáticamente durante esta reparación.


## Error de constructor en ClienteMergeService (20/10/2026)

Se reemplazo `@RequiredArgsConstructor` por un constructor explicito con las nueve
dependencias, compatible con el nivel Java 17 del proyecto. El archivo de clase del
editor incluia un error diferido `Unresolved compilation problems` referido a
`Flexible Constructor Bodies`; no se necesita cambiar el proyecto a Java 25.

El verificador ahora detecta tambien clases cargables que contienen esos errores
del compilador. Se reparo `target/classes` desde Maven, conservando los archivos
anteriores en cuarentena. Se verificaron 1139 clases sin errores y se instancio
el servicio con nueve repositorios simulados, comprobando su asignacion. No se
inicio Spring ni se ejecutaron fusiones de clientes.

En la configuracion del proyecto se desactivo continuar la depuracion tras fallos
de compilacion y la reconstruccion automatica antes de depurar. Para compilar e
iniciar se recomienda `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/start-local.ps1`.
