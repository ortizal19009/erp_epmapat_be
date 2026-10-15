param([string]$PostgresBin = 'C:\Program Files\PostgreSQL\18\bin')
$ErrorActionPreference = 'Stop'
$taskWorkspace = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskCluster = Join-Path $taskWorkspace ('target\employee-files-pg-' + [guid]::NewGuid().ToString('N'))
$taskStarted = $false
if (Get-NetTCPConnection -LocalPort 55443 -State Listen -ErrorAction SilentlyContinue) { throw 'Puerto temporal 55443 ocupado.' }
Push-Location -LiteralPath $taskWorkspace
function Invoke-FixtureSql([string]$Sql) {
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55443 -U employee_files_test -d employee_files_test -v ON_ERROR_STOP=1 -c $Sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo SQL en la base temporal.' }
}
try {
    New-Item -ItemType Directory -Path (Join-Path $taskWorkspace 'target') -Force | Out-Null
    & (Join-Path $PostgresBin 'initdb.exe') -D $taskCluster -U employee_files_test -A trust --no-locale -E UTF8
    if ($LASTEXITCODE -ne 0) { throw 'Fallo initdb.' }
    $taskCtl = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -WindowStyle Hidden -PassThru `
        -ArgumentList @('-D',('"'+$taskCluster+'"'),'-l',('"'+$taskCluster+'\server.log"'),'-o','"-p 55443 -h 127.0.0.1"','-w','start') `
        -RedirectStandardOutput (Join-Path $taskCluster 'startup.log') -RedirectStandardError (Join-Path $taskCluster 'startup-error.log')
    $taskCtl.WaitForExit(30000) | Out-Null
    $taskStarted = Test-Path -LiteralPath (Join-Path $taskCluster 'postmaster.pid')
    if (-not $taskStarted) { throw 'No inicio PostgreSQL temporal.' }
    & (Join-Path $PostgresBin 'createdb.exe') -h 127.0.0.1 -p 55443 -U employee_files_test employee_files_test
    if ($LASTEXITCODE -ne 0) { throw 'Fallo createdb.' }
    # Native PostgreSQL notices on stderr are informational, not test failures.
    $ErrorActionPreference = 'Continue'
    Invoke-FixtureSql @'
CREATE TABLE personal(idpersonal BIGINT PRIMARY KEY); INSERT INTO personal VALUES (7);
CREATE TABLE IF NOT EXISTS th_employee_files (
    idfile BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipo_doc VARCHAR(100),
    nombre_archivo VARCHAR(255),
    ruta_archivo TEXT,
    hash_archivo VARCHAR(255),
    version_doc INTEGER,
    estado VARCHAR(30),
    feccrea DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    CONSTRAINT fk_th_employee_files_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal)
);
INSERT INTO th_employee_files(idpersonal_personal,tipo_doc,nombre_archivo,ruta_archivo,version_doc,estado) VALUES(7,'CONTRATO','Contrato anterior','archivo/anterior.pdf',1,'ACTIVO');
'@
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55443 -U employee_files_test -d employee_files_test -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-15_th_employee_files_metadata.sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo migracion inicial.' }
    Invoke-FixtureSql "UPDATE th_employee_files SET descripcion='Documento anterior',emisor='Talento Humano',fecha_emision='2026-01-01',fecha_vencimiento='2027-01-01' WHERE idfile=1;"
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55443 -U employee_files_test -d employee_files_test -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-15_th_employee_files_metadata.sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo repeticion de migracion.' }
    Invoke-FixtureSql @'
DO $$ BEGIN
 IF (SELECT count(*) FROM th_employee_files WHERE nombre_archivo='Contrato anterior' AND descripcion='Documento anterior' AND version_doc=1 AND origen IS NULL) <> 1 THEN
   RAISE EXCEPTION 'No se preservo el documento anterior y sus metadatos';
 END IF;
 IF (SELECT count(*) FROM information_schema.columns WHERE table_schema='public' AND table_name='th_employee_files' AND column_name IN ('descripcion','numero_documento','emisor','fecha_emision','fecha_vencimiento','origen','nombre_original','content_type','tamano_bytes')) <> 9 THEN
   RAISE EXCEPTION 'Metadatos incompletos';
 END IF;
 BEGIN
   UPDATE th_employee_files SET idpersonal_personal=999;
   RAISE EXCEPTION 'Se acepto empleado inexistente';
 EXCEPTION WHEN foreign_key_violation THEN NULL; END;
END $$;
'@
    Write-Output 'PostgreSQL: documento anterior conservado, metadatos persistidos, migracion repetible y relacion con personal verificadas.'
} finally {
    if ($taskStarted) { & (Join-Path $PostgresBin 'pg_ctl.exe') -D $taskCluster -m fast -w stop }
    Pop-Location
}
