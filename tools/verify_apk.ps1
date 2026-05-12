$ErrorActionPreference = 'Stop'
$btDir = "$env:LOCALAPPDATA\Android\Sdk\build-tools"
$ver = (Get-ChildItem $btDir -Directory | Sort-Object Name -Descending | Select-Object -First 1).FullName
$apksigner = Join-Path $ver 'apksigner.bat'
Write-Host "Using: $apksigner"
& $apksigner verify --verbose "D:\Android Apps\ESEC\app\build\outputs\apk\release\app-release.apk"
Write-Host "---PRINT CERTS---"
& $apksigner verify --print-certs "D:\Android Apps\ESEC\app\build\outputs\apk\release\app-release.apk"
