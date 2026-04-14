$baseUrl = "http://localhost:8081"
$headers = @{ "Content-Type" = "application/json" }
$ts = Get-Date -Format "HHmmss"

function Test-Endpoint {
    param([string]$Method, [string]$Uri, [string]$Body, [string]$EntityName)
    
    Write-Host "Testing $Method $Uri ($EntityName)..." -NoNewline
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Method $Method -Uri "$baseUrl$Uri" -Headers $headers -Body $Body -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Method $Method -Uri "$baseUrl$Uri" -Headers $headers -ErrorAction Stop
        }
        Write-Host " OK! (Code: 200/201, ID: $($response.data.id))" -ForegroundColor Green
        return $response.data
    } catch {
        Write-Host " FAILED!" -ForegroundColor Red
        Write-Host $_.Exception.Response.StatusCode
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host $reader.ReadToEnd() -ForegroundColor Yellow
        return $null
    }
}

Write-Host "==============================================="
Write-Host " SmartLogix API - Automated End-to-End Test "
Write-Host "==============================================="

# 1. Create User (for Driver)
$userBody = @{
    name = "Alice Driver $ts"
    role = "DRIVER"
    email = "alice.driver.$ts@example.com"
    phone = "555-$ts"
    password = "securePassword123!"
} | ConvertTo-Json
$user = Test-Endpoint -Method POST -Uri "/users" -Body $userBody -EntityName "User"

# 2. Create Merchant
$merchantBody = @{
    name = "Acme Retail $ts"
    contactInfoJson = "{`"phone`": `"555-0001`"}"
    billingTermsJson = "{`"net`": 30}"
} | ConvertTo-Json
$merchant = Test-Endpoint -Method POST -Uri "/merchants" -Body $merchantBody -EntityName "Merchant"

# 3. Create Service Zone
$zoneBody = @{
    name = "Downtown Core $ts"
    timeZone = "America/New_York"
    capacityPerSlot = 50
    polygonGeoJson = "{`"type`": `"Polygon`"}"
} | ConvertTo-Json
$zone = Test-Endpoint -Method POST -Uri "/zones" -Body $zoneBody -EntityName "Service Zone"

# 4. Create Depot
$depotBody = @{
    name = "Central Hub $ts"
    timeZone = "America/New_York"
    addressJson = "{`"city`": `"New York`"}"
} | ConvertTo-Json
$depot = Test-Endpoint -Method POST -Uri "/depots" -Body $depotBody -EntityName "Depot"

# 5. Create Vehicle
$vehicleBody = @{
    type = "VAN"
    capacityKg = 1500.0
    capacityVolumeM3 = 12.5
    registrationNumber = "XYZ-$ts"
} | ConvertTo-Json
$vehicle = Test-Endpoint -Method POST -Uri "/vehicles" -Body $vehicleBody -EntityName "Vehicle"

# 6. Create Driver (Requires User)
if ($user) {
    $driverBody = @{
        userId = $user.id
        licenseNumber = "LIC-987654"
        phone = "555-0100"
        maxDailyHours = 8.0
    } | ConvertTo-Json
    $driver = Test-Endpoint -Method POST -Uri "/drivers" -Body $driverBody -EntityName "Driver"
}

# 7. Create Fulfillment (Requires Merchant & Zone)
if ($merchant -and $zone) {
    $fulfillmentBody = @{
        orderId = "ORD-$ts"
        merchantId = $merchant.id
        serviceZoneId = $zone.id
        serviceLevel = "NEXT_DAY"
        packageWeightKg = 2.5
        packageVolumeM3 = 0.05
        deliveryWindowStart = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:00:00")
        deliveryWindowEnd = (Get-Date).AddDays(1).AddHours(4).ToString("yyyy-MM-ddTHH:00:00")
    } | ConvertTo-Json
    $fulfillment = Test-Endpoint -Method POST -Uri "/fulfillments" -Body $fulfillmentBody -EntityName "Fulfillment"
}

# 8. GET Check for Paged Endpoints
Write-Host "-----------------------------------------------"
Write-Host "Running GET checks on paginated collections..."
$null = Test-Endpoint -Method GET -Uri "/fulfillments" -Body "" -EntityName "List Fulfillments"
$null = Test-Endpoint -Method GET -Uri "/merchants" -Body "" -EntityName "List Merchants"
$null = Test-Endpoint -Method GET -Uri "/zones" -Body "" -EntityName "List Zones"

Write-Host "==============================================="
Write-Host " Automated Test Run Complete "
Write-Host "==============================================="
