$ErrorActionPreference = "Stop"

param(
    [string]$NextcloudBaseUrl = "https://192.168.0.52",
    [string]$NextcloudUsername = "",
    [string]$NextcloudAppToken = "",
    [string]$NextcloudBaseFolder = "/epmapat",
    [string]$SpringProfile = "local",
    [string]$StorageType = "nextcloud",
    [string]$MavenArgs = "spring-boot:run"
)

if ([string]::IsNullOrWhiteSpace($NextcloudUsername)) {
    throw "Debes indicar -NextcloudUsername"
}

if ([string]::IsNullOrWhiteSpace($NextcloudAppToken)) {
    throw "Debes indicar -NextcloudAppToken"
}

$env:SPRING_PROFILES_ACTIVE = $SpringProfile
$env:STORAGE_TYPE = $StorageType
$env:NEXTCLOUD_BASE_URL = $NextcloudBaseUrl
$env:NEXTCLOUD_USERNAME = $NextcloudUsername
$env:NEXTCLOUD_APP_TOKEN = $NextcloudAppToken
$env:NEXTCLOUD_BASE_FOLDER = $NextcloudBaseFolder

Write-Host "Iniciando backend con Nextcloud..." -ForegroundColor Cyan
Write-Host "SPRING_PROFILES_ACTIVE=$env:SPRING_PROFILES_ACTIVE"
Write-Host "STORAGE_TYPE=$env:STORAGE_TYPE"
Write-Host "NEXTCLOUD_BASE_URL=$env:NEXTCLOUD_BASE_URL"
Write-Host "NEXTCLOUD_USERNAME=$env:NEXTCLOUD_USERNAME"
Write-Host "NEXTCLOUD_BASE_FOLDER=$env:NEXTCLOUD_BASE_FOLDER"

& .\mvnw.cmd $MavenArgs
