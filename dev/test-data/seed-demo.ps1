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

docker info *> $null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker Engine is not running. Start Docker Desktop and try again.'
}

Invoke-SeedSql -Container 'cruise_postgres_tour' -Database 'tour_service_db' -FileName 'seed-tour.sql'
Invoke-SeedSql -Container 'cruise_postgres_booking' -Database 'booking_service_db' -FileName 'seed-booking.sql'
Invoke-SeedSql -Container 'cruise_postgres_payment' -Database 'payment_service_db' -FileName 'seed-payment.sql'

Write-Host 'FLOW demo data is ready.' -ForegroundColor Green
