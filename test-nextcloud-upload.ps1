$ErrorActionPreference = "Stop"

param(
    [string]$BackendBaseUrl = "http://localhost:9080",
    [string]$FilePath,
    [string]$Folder = "lecturas/prueba"
)

if ([string]::IsNullOrWhiteSpace($FilePath)) {
    throw "Debes indicar -FilePath con la ruta de una imagen o archivo de prueba"
}

if (!(Test-Path -LiteralPath $FilePath)) {
    throw "No existe el archivo: $FilePath"
}

$url = $BackendBaseUrl.TrimEnd("/") + "/api/storage/upload"

Write-Host "Probando upload a $url" -ForegroundColor Cyan
Write-Host "Archivo: $FilePath"
Write-Host "Folder: $Folder"

$response = Invoke-RestMethod `
    -Uri $url `
    -Method Post `
    -Form @{
        file = Get-Item -LiteralPath $FilePath
        folder = $Folder
    }

$response | ConvertTo-Json -Depth 6
