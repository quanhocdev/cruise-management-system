$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080'
$paths = @{
    admin='/api/admin/cruises'; scheduler='/api/scheduler/tours'
    operation='/api/operation/tours/pending'; finance='/api/finance/tours'
    convenience='/api/convenience/services'; onboard='/api/onboard/activity-cruise-tours'
    shore='/api/shore/visit-tours'; passenger='/api/passengers/bookings'
}
foreach ($role in $paths.Keys) {
    $login = Invoke-RestMethod "$base/api/auth/login" -Method Post -ContentType 'application/json' -Body (@{username="demo.$role";password='admin@123'} | ConvertTo-Json)
    $rows = Invoke-RestMethod ($base+$paths[$role]) -Headers @{Authorization="Bearer $($login.token)"}
    Write-Host "$role API OK ($(@($rows).Count) records, including existing data)"
}
$tours = Invoke-RestMethod "$base/api/public/tours"
$sea = @($tours | Where-Object { $_.code -match '^SEA-0[1-6]$' })
if ($sea.Count -ne 6) { throw 'Expected 6 public SEA tours.' }
foreach ($tour in $sea) {
    $d = Invoke-RestMethod "$base/api/public/tours/$($tour.id)"
    if ($d.packages.Count -ne 3 -or $d.schedules.Count -ne 3 -or $d.services.Count -ne 8 -or $d.products.Count -ne 12 -or $d.onboardActivities.Count -ne 3) {
        throw "Incomplete catalog for $($tour.code)"
    }
    foreach ($p in $d.packages) {
        if ($p.benefits.Count -ne 4) { throw "Missing benefits for $($p.id)" }
    }
    Write-Host "$($tour.code): catalog and package benefits OK"
}
