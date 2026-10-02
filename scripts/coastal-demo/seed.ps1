[CmdletBinding()]
param()
$ErrorActionPreference = 'Stop'
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
function RunSql([string]$service, [string]$file) {
    Get-Content -Raw -Encoding UTF8 -LiteralPath (Join-Path $PSScriptRoot $file) |
        docker exec -i "cruise_postgres_$service" psql -X -U postgres -d "${service}_service_db" -v ON_ERROR_STOP=1
    if ($LASTEXITCODE -ne 0) { throw "Failed: $file. Earlier databases may already be seeded; rerun safely after fixing the error." }
}
RunSql 'auth' 'auth.sql'
RunSql 'tour' 'tour.sql'
RunSql 'booking' 'booking.sql'
RunSql 'payment' 'payment.sql'

# Count physical rooms and subtract ALL existing non-cancelled bookings, including manual tests.
$catalog = docker exec cruise_postgres_tour psql -X -U postgres -d tour_service_db -At -F '|' -c "SELECT p.id,count(r.id) FROM tour.tour_packages p JOIN tour.tours t ON t.id=p.tour_id JOIN tour.cruise_decks d ON d.cruise_id=t.cruise_id JOIN tour.rooms r ON r.cruise_deck_id=d.id AND r.room_type_id=p.room_type_id AND r.status='ACTIVE' WHERE t.code ~ '^SEA-[0-9]{2}$' GROUP BY p.id;"
if ($LASTEXITCODE -ne 0) { throw 'Cannot read room inventory.' }
foreach ($row in $catalog) {
    $parts = $row.Split('|')
    $packageId = [guid]::Parse($parts[0]).ToString()
    $reserved = docker exec cruise_postgres_booking psql -X -U postgres -d booking_service_db -At -c "SELECT coalesce(sum(number_of_rooms),0) FROM booking.bookings WHERE tour_package_id='$packageId' AND status<>'CANCELLED';"
    if ($LASTEXITCODE -ne 0) { throw 'Cannot count reservations.' }
    $remaining = [Math]::Max(0, [int]$parts[1] - [int]$reserved)
    docker exec cruise_redis redis-cli SET "tour:package:$($packageId):available_rooms" $remaining | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'Cannot write Redis inventory.' }
}
Write-Host 'SEA demo ready. Existing rows preserved. Redis reconciled with real room counts.'
