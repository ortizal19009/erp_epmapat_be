param(
    [switch]$VerifyOnly,
    [switch]$RepairDefaultClasses
)
$ErrorActionPreference = 'Stop'
$taskRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$taskBuild = Join-Path $taskRoot 'target\local-runtime'
$taskClasses = Join-Path $taskBuild 'classes'
$taskClasspathFile = Join-Path $taskBuild 'classpath.txt'
$taskLog = Join-Path $taskBuild 'compilacion.log'
$taskEnv = Join-Path $taskRoot '.env.local'

# Read values as literal strings: never execute the dotenv file as shell code.
if (Test-Path -LiteralPath $taskEnv) {
    foreach ($taskLine in [IO.File]::ReadAllLines($taskEnv)) {
        if ($taskLine -notmatch '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=(.*)$') { continue }
        $taskKey = $Matches[1]
        if ($taskKey -in @('HOME','CODEX_HOME','PATH','USERPROFILE','SYSTEMROOT')) { continue }
        $taskValue = $Matches[2].Trim()
        if ($taskValue.Length -ge 2 -and (($taskValue.StartsWith('"') -and $taskValue.EndsWith('"')) -or ($taskValue.StartsWith("'") -and $taskValue.EndsWith("'")))) {
            $taskValue = $taskValue.Substring(1, $taskValue.Length - 2)
        }
        [Environment]::SetEnvironmentVariable($taskKey,$taskValue,'Process')
    }
}
$env:SPRING_PROFILES_ACTIVE = 'local'
Push-Location -LiteralPath $taskRoot
try {
    New-Item -ItemType Directory -Path $taskBuild -Force | Out-Null
    Write-Host 'Compilando y verificando clases en target/local-runtime...'
    # Capture native warnings without treating stderr as a PowerShell exception.
    $ErrorActionPreference = 'Continue'
    & mvn '-Plocal-runtime' '-DskipTests' 'compile' 'dependency:build-classpath' "-Dmdep.outputFile=$taskClasspathFile" *> $taskLog
    $taskExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($taskExit -ne 0) { throw "Fallo la compilacion. Revise $taskLog" }
    $taskClasspath = $taskClasses + [IO.Path]::PathSeparator + [IO.File]::ReadAllText($taskClasspathFile).Trim()
    & java '--class-path' $taskClasspath (Join-Path $PSScriptRoot 'java\VerifyCompiledClasses.java') $taskClasses
    if ($LASTEXITCODE -ne 0) { throw 'Existen clases invalidas. No se inicia Spring.' }
    if ($RepairDefaultClasses) {
        $taskDefault = [IO.Path]::GetFullPath((Join-Path $taskRoot 'target\classes'))
        $taskQuarantine = [IO.Path]::GetFullPath((Join-Path $taskRoot ('target\classes-invalidas-' + [guid]::NewGuid().ToString('N'))))
        $taskPrefix = $taskRoot.TrimEnd('\') + '\'
        foreach ($taskPath in @($taskDefault,$taskQuarantine,$taskClasses)) {
            if (-not $taskPath.StartsWith($taskPrefix,[StringComparison]::OrdinalIgnoreCase)) { throw 'Ruta de clases fuera del proyecto.' }
        }
        foreach ($taskPath in @((Join-Path $taskRoot 'target'),$taskDefault)) {
            if ((Test-Path -LiteralPath $taskPath) -and ((Get-Item -LiteralPath $taskPath).Attributes -band [IO.FileAttributes]::ReparsePoint)) { throw 'No se modifican directorios de clases con enlaces.' }
        }
        if (Test-Path -LiteralPath $taskDefault) { Move-Item -LiteralPath $taskDefault -Destination $taskQuarantine }
        Copy-Item -LiteralPath $taskClasses -Destination $taskDefault -Recurse
        $taskDefaultClasspath = $taskDefault + [IO.Path]::PathSeparator + [IO.File]::ReadAllText($taskClasspathFile).Trim()
        & java '--class-path' $taskDefaultClasspath (Join-Path $PSScriptRoot 'java\VerifyCompiledClasses.java') $taskDefault
        if ($LASTEXITCODE -ne 0) { throw 'El editor esta regenerando clases invalidas; use el arranque aislado y recargue la configuracion del proyecto.' }
        Write-Host 'target/classes verificado; clases anteriores conservadas en target/classes-invalidas-*.'
    }
    if (-not $VerifyOnly) {
        Write-Host 'Iniciando Spring con perfil local y clases aisladas...'
        & mvn '-Plocal-runtime' 'spring-boot:run' '-Dspring-boot.run.profiles=local'
        if ($LASTEXITCODE -ne 0) { throw 'El backend termino con error. Revise el log de arranque.' }
    }
} finally {
    Pop-Location
}
