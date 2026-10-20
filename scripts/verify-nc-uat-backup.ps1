param(
    [string]$PostgresBin = 'C:\Program Files\PostgreSQL\18\bin',
    [Parameter(Mandatory=$true)][string]$Backup,
    [int]$ExpectedNotes = 98
)

$ErrorActionPreference = 'Stop'
$taskBackup = (Resolve-Path -LiteralPath $Backup).Path
$taskWorkspace = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskCluster = Join-Path $taskWorkspace ('target\nc-restore-pg-' + [guid]::NewGuid().ToString('N'))
$taskLog = Join-Path $taskCluster 'server.log'
$taskStarted = $false

foreach ($binary in @('initdb.exe', 'pg_ctl.exe', 'createdb.exe', 'pg_restore.exe', 'psql.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $PostgresBin $binary))) {
        throw "No se encuentra $binary en $PostgresBin"
    }
}
if (Get-NetTCPConnection -LocalPort 55443 -State Listen -ErrorAction SilentlyContinue) {
    throw 'El puerto de pruebas 55443 ya esta ocupado. No se utilizara esa instancia.'
}

Push-Location -LiteralPath $taskWorkspace
try {
    New-Item -ItemType Directory -Path (Join-Path $taskWorkspace 'target') -Force | Out-Null
    & (Join-Path $PostgresBin 'initdb.exe') -D $taskCluster -U refacturaciones_test -A trust --no-locale -E UTF8
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo inicializar el cluster temporal.' }
    # Wait only for pg_ctl, not for its persistent PostgreSQL child process.
    $taskCtl = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -WindowStyle Hidden -PassThru `
        -ArgumentList @('-D', ('"' + $taskCluster + '"'), '-l', ('"' + $taskLog + '"'),
            '-o', '"-p 55443 -h 127.0.0.1"', '-w', 'start') `
        -RedirectStandardOutput (Join-Path $taskCluster 'startup.log') `
        -RedirectStandardError (Join-Path $taskCluster 'startup-error.log')
    $taskExited = $taskCtl.WaitForExit(30000)
    $taskStarted = Test-Path -LiteralPath (Join-Path $taskCluster 'postmaster.pid')
    if (-not $taskExited -or -not $taskStarted) { throw "No se pudo iniciar el cluster temporal. Revise $taskCluster" }
    if ($null -ne $taskCtl.ExitCode -and $taskCtl.ExitCode -ne 0) { throw 'Fallo pg_ctl al iniciar.' }
    & (Join-Path $PostgresBin 'createdb.exe') -h 127.0.0.1 -p 55443 -U refacturaciones_test refacturaciones_test
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear la base de pruebas.' }
    & (Join-Path $PostgresBin 'pg_restore.exe') -h 127.0.0.1 -p 55443 -U refacturaciones_test -d refacturaciones_test --no-owner --no-privileges --section=pre-data --section=data --exit-on-error $taskBackup
    if ($LASTEXITCODE -ne 0) { throw 'Fallo la restauracion del respaldo.' }
    $taskCount = & (Join-Path $PostgresBin 'psql.exe') -X -A -t -h 127.0.0.1 -p 55443 -U refacturaciones_test -d refacturaciones_test -v ON_ERROR_STOP=1 -c 'SELECT count(*) FROM public.ntacredito'
    if ($LASTEXITCODE -ne 0 -or [int]$taskCount -ne $ExpectedNotes) { throw 'El respaldo restaurado no contiene las notas esperadas.' }
    Write-Output "Respaldo restaurado en PostgreSQL temporal: $taskCount notas. No se modifico localhost:5432."

} finally {
    if ($taskStarted) {
        & (Join-Path $PostgresBin 'pg_ctl.exe') -D $taskCluster -m fast -w stop
        if ($LASTEXITCODE -ne 0) { Write-Warning "Revise el cluster temporal: $taskCluster" }
    }
    Pop-Location
}

# The stopped cluster remains under ignored target/ for inspection; no production settings are read.
