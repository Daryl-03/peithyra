# Example: ./scripts/new-module.ps1 -Name user
# Preview: ./scripts/new-module.ps1 -Name user -WhatIf
[CmdletBinding(SupportsShouldProcess)]
param(
    [Parameter(Mandatory)]
    [ValidatePattern('^[a-z][a-z0-9_]*$')]
    [string]$Name
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$packageRoot = Join-Path $projectRoot 'backend/src/main/java/com/peithyra/api'

if (-not (Test-Path -LiteralPath $packageRoot -PathType Container)) {
    throw "Java package root not found: $packageRoot"
}

$moduleRoot = Join-Path $packageRoot $Name
$packages = @(
    'internal/adapters/persistence/jpa'
    'internal/adapters/web/dto'
    'internal/application/dto'
    'internal/application/port/in'
    'internal/application/port/out'
    'internal/domain/exceptions'
)

foreach ($package in $packages) {
    $directory = Join-Path $moduleRoot $package
    if (Test-Path -LiteralPath $directory -PathType Container) {
        continue
    }

    if ($PSCmdlet.ShouldProcess($directory, 'Create package directory')) {
        New-Item -ItemType Directory -Path $directory -Force | Out-Null
    }
}
