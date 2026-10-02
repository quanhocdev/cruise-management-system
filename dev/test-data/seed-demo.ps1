[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

function Invoke-SeedSql {
    param(
        [Parameter(Mandatory)][string]$Container,
        [Parameter(Mandatory)][string]$Database,
        [Parameter(Mandatory)][string]$FileName
    )

    $filePath = Join-Path $scriptRoot $FileName
    Write-Host "Seeding $Database from $FileName ..." -ForegroundColor Cyan
    Get-Content -Raw -LiteralPath $filePath | docker exec -i $Container psql -U postgres -d $Database
    if ($LASTEXITCODE -ne 0) {
        throw "Seed failed for $Database ($FileName)."
    }
}

function Get-Md5Uuid {
    param([Parameter(Mandatory)][string]$Value)
    $md5 = [System.Security.Cryptography.MD5]::Create()
    try {
        $hex = ([BitConverter]::ToString(
            $md5.ComputeHash([Text.Encoding]::UTF8.GetBytes($Value))
        )).Replace('-', '').ToLowerInvariant()
        return '{0}-{1}-{2}-{3}-{4}' -f
            $hex.Substring(0, 8), $hex.Substring(8, 4), $hex.Substring(12, 4),
            $hex.Substring(16, 4), $hex.Substring(20, 12)
    } finally {
        $md5.Dispose()
    }
}

docker info *> $null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker Engine is not running. Start Docker Desktop and try again.'
}

Invoke-SeedSql -Container 'cruise_postgres_tour' -Database 'tour_service_db' -FileName 'seed-tour.sql'
Invoke-SeedSql -Container 'cruise_postgres_booking' -Database 'booking_service_db' -FileName 'seed-booking.sql'
Invoke-SeedSql -Container 'cruise_postgres_payment' -Database 'payment_service_db' -FileName 'seed-payment.sql'

Write-Host 'Initializing Redis room inventory for FLOW packages ...' -ForegroundColor Cyan
1..12 | ForEach-Object {
    $packageId = Get-Md5Uuid -Value "flow-package-$_"
    docker exec cruise_redis redis-cli SET "tour:package:$($packageId):available_rooms" 10 *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Redis inventory initialization failed for FLOW package $_."
    }
}

Write-Host 'FLOW demo data is ready.' -ForegroundColor Green
