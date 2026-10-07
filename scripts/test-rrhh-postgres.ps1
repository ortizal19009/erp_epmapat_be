param(
    [string]$PostgresBin = 'C:\Program Files\PostgreSQL\18\bin'
)

$ErrorActionPreference = 'Stop'
$taskWorkspace = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskCluster = Join-Path $taskWorkspace ('target\rrhh-pg-' + [guid]::NewGuid().ToString('N'))
$taskLog = Join-Path $taskCluster 'server.log'
$taskStarted = $false
$taskOldFlag = $env:RRHH_TEST_POSTGRES

foreach ($binary in @('initdb.exe', 'pg_ctl.exe', 'createdb.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $PostgresBin $binary))) {
        throw "No se encuentra $binary en $PostgresBin"
    }
}
if (Get-NetTCPConnection -LocalPort 55439 -State Listen -ErrorAction SilentlyContinue) {
    throw 'El puerto de pruebas 55439 ya esta ocupado. No se utilizara esa instancia.'
}

Push-Location -LiteralPath $taskWorkspace
try {
    New-Item -ItemType Directory -Path (Join-Path $taskWorkspace 'target') -Force | Out-Null
    & (Join-Path $PostgresBin 'initdb.exe') -D $taskCluster -U rrhh_test -A trust --no-locale -E UTF8
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo inicializar el cluster temporal.' }
    # Wait only for pg_ctl, not for its persistent PostgreSQL child process.
    $taskCtl = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -WindowStyle Hidden -PassThru `
        -ArgumentList @('-D', ('"' + $taskCluster + '"'), '-l', ('"' + $taskLog + '"'),
            '-o', '"-p 55439 -h 127.0.0.1"', '-w', 'start') `
        -RedirectStandardOutput (Join-Path $taskCluster 'startup.log') `
        -RedirectStandardError (Join-Path $taskCluster 'startup-error.log')
    $taskExited = $taskCtl.WaitForExit(30000)
    $taskStarted = Test-Path -LiteralPath (Join-Path $taskCluster 'postmaster.pid')
    if (-not $taskExited -or -not $taskStarted) { throw "No se pudo iniciar el cluster temporal. Revise $taskCluster" }
    if ($null -ne $taskCtl.ExitCode -and $taskCtl.ExitCode -ne 0) { throw 'Fallo pg_ctl al iniciar.' }
    & (Join-Path $PostgresBin 'createdb.exe') -h 127.0.0.1 -p 55439 -U rrhh_test rrhh_test
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear la base de pruebas.' }
    $env:RRHH_TEST_POSTGRES = 'true'
    # Windows PowerShell treats native stderr warnings as errors when output is redirected.
    $ErrorActionPreference = 'Continue'
    try {
        & mvn '-Dtest=ThLeave*Test' test
        $taskTestExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = 'Stop' }
    if ($taskTestExit -ne 0) { throw 'Fallaron las pruebas de RRHH.' }
    $taskReports = Get-ChildItem (Join-Path $taskWorkspace 'target\surefire-reports') -Filter 'TEST-*ThLeave*.xml' |
        ForEach-Object {
            [xml]$taskXml = Get-Content -LiteralPath $_.FullName -Raw
            [pscustomobject]@{
                Tests = [int]$taskXml.testsuite.tests
                Failed = [int]$taskXml.testsuite.failures + [int]$taskXml.testsuite.errors
                Skipped = [int]$taskXml.testsuite.skipped
            }
        }
    $taskTotal = ($taskReports | Measure-Object -Property Tests -Sum).Sum
    $taskFailed = ($taskReports | Measure-Object -Property Failed -Sum).Sum
    $taskSkipped = ($taskReports | Measure-Object -Property Skipped -Sum).Sum
    if ($taskTotal -lt 122 -or $taskFailed -gt 0 -or $taskSkipped -gt 0) {
        throw 'No se ejecutaron correctamente todas las pruebas de RRHH, incluida la integracion.'
    }
    Write-Output "RRHH: $taskTotal pruebas aprobadas, incluidas las de PostgreSQL."
} finally {
    $env:RRHH_TEST_POSTGRES = $taskOldFlag
    if ($taskStarted) {
        & (Join-Path $PostgresBin 'pg_ctl.exe') -D $taskCluster -m fast -w stop
        if ($LASTEXITCODE -ne 0) { Write-Warning "Revise el cluster temporal: $taskCluster" }
    }
    Pop-Location
}

# The stopped cluster remains under ignored target/ for inspection; no production settings are read.
