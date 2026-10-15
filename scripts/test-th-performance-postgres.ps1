param([string]$PostgresBin = 'C:\Program Files\PostgreSQL\18\bin')
$ErrorActionPreference = 'Stop'
$taskWorkspace = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskCluster = Join-Path $taskWorkspace ('target\performance-pg-' + [guid]::NewGuid().ToString('N'))
$taskStarted = $false
if (Get-NetTCPConnection -LocalPort 55442 -State Listen -ErrorAction SilentlyContinue) { throw 'Puerto temporal 55442 ocupado.' }
Push-Location -LiteralPath $taskWorkspace
function Invoke-FixtureSql([string]$Sql) {
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55442 -U performance_test -d performance_test -v ON_ERROR_STOP=1 -c $Sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo SQL en la base temporal.' }
}
try {
    New-Item -ItemType Directory -Path (Join-Path $taskWorkspace 'target') -Force | Out-Null
    & (Join-Path $PostgresBin 'initdb.exe') -D $taskCluster -U performance_test -A trust --no-locale -E UTF8
    if ($LASTEXITCODE -ne 0) { throw 'Fallo initdb.' }
    $taskCtl = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -WindowStyle Hidden -PassThru `
        -ArgumentList @('-D',('"'+$taskCluster+'"'),'-l',('"'+$taskCluster+'\server.log"'),'-o','"-p 55442 -h 127.0.0.1"','-w','start') `
        -RedirectStandardOutput (Join-Path $taskCluster 'startup.log') -RedirectStandardError (Join-Path $taskCluster 'startup-error.log')
    $taskCtl.WaitForExit(30000) | Out-Null
    $taskStarted = Test-Path -LiteralPath (Join-Path $taskCluster 'postmaster.pid')
    if (-not $taskStarted) { throw 'No inicio PostgreSQL temporal.' }
    & (Join-Path $PostgresBin 'createdb.exe') -h 127.0.0.1 -p 55442 -U performance_test performance_test
    if ($LASTEXITCODE -ne 0) { throw 'Fallo createdb.' }
    # Native PostgreSQL notices on stderr are informational, not test failures.
    $ErrorActionPreference = 'Continue'
    Invoke-FixtureSql 'CREATE TABLE personal(idpersonal BIGINT PRIMARY KEY); INSERT INTO personal VALUES (7);'
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55442 -U performance_test -d performance_test -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-15_th_performance_reviews.sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo migracion inicial.' }
    Invoke-FixtureSql "INSERT INTO th_performance_reviews(idpersonal,periodo,fecha,puntaje,evaluador,observaciones,plan_mejora,usuario,creado) VALUES(7,'2026','2026-10-15',90.25,'Jefatura','Cumple objetivos','Seguimiento',8,now());"
    & (Join-Path $PostgresBin 'psql.exe') -X -h 127.0.0.1 -p 55442 -U performance_test -d performance_test -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-15_th_performance_reviews.sql
    if ($LASTEXITCODE -ne 0) { throw 'Fallo repeticion de migracion.' }
    Invoke-FixtureSql @'
DO $$ BEGIN
 IF (SELECT count(*) FROM th_performance_reviews WHERE idpersonal=7 AND puntaje=90.25 AND usuario=8) <> 1 THEN
   RAISE EXCEPTION 'No se preservo la evaluacion';
 END IF;
 BEGIN
   UPDATE th_performance_reviews SET puntaje=101;
   RAISE EXCEPTION 'Se acepto puntaje invalido';
 EXCEPTION WHEN check_violation THEN NULL; END;
 BEGIN
   UPDATE th_performance_reviews SET idpersonal=999;
   RAISE EXCEPTION 'Se acepto empleado inexistente';
 EXCEPTION WHEN foreign_key_violation THEN NULL; END;
 BEGIN
   UPDATE th_performance_reviews SET observaciones='';
   RAISE EXCEPTION 'Se acepto observacion vacia';
 EXCEPTION WHEN check_violation THEN NULL; END;
END $$;
'@
    Write-Output 'PostgreSQL: registro, lectura, migracion repetida, rango de puntaje, empleado y observaciones verificados.'
} finally {
    if ($taskStarted) { & (Join-Path $PostgresBin 'pg_ctl.exe') -D $taskCluster -m fast -w stop }
    Pop-Location
}
