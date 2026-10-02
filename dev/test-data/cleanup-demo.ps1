[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

function Invoke-CleanupSql {
    param([string]$Container, [string]$Database, [string]$FileName)
    $filePath = Join-Path $scriptRoot $FileName
    Write-Host "Cleaning $Database from $FileName ..." -ForegroundColor Yellow
    Get-Content -Raw -LiteralPath $filePath | docker exec -i $Container psql -U postgres -d $Database
    if ($LASTEXITCODE -ne 0) { throw "Cleanup failed for $Database ($FileName)." }
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

# Cross-service references are removed before the tour catalog.
Invoke-CleanupSql 'cruise_postgres_payment' 'payment_service_db' 'cleanup-payment.sql'
Invoke-CleanupSql 'cruise_postgres_booking' 'booking_service_db' 'cleanup-booking.sql'
Invoke-CleanupSql 'cruise_postgres_tour' 'tour_service_db' 'cleanup-tour.sql'
1..12 | ForEach-Object {
    $packageId = Get-Md5Uuid -Value "flow-package-$_"
    docker exec cruise_redis redis-cli DEL "tour:package:$($packageId):available_rooms" *> $null
    if ($LASTEXITCODE -ne 0) { throw "Redis cleanup failed for FLOW package $_." }
}
Write-Host 'FLOW demo data was removed.' -ForegroundColor Green
