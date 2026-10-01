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

# Cross-service references are removed before the tour catalog.
Invoke-CleanupSql 'cruise_postgres_payment' 'payment_service_db' 'cleanup-payment.sql'
Invoke-CleanupSql 'cruise_postgres_booking' 'booking_service_db' 'cleanup-booking.sql'
Invoke-CleanupSql 'cruise_postgres_tour' 'tour_service_db' 'cleanup-tour.sql'
Write-Host 'FLOW demo data was removed.' -ForegroundColor Green
