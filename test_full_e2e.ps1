
$baseUrl = "http://localhost:8081"
$headers = @{ "Content-Type" = "application/json" }
$ts = Get-Date -Format "HHmmss"
$date = Get-Date -Format "yyyy-MM-dd"
$tomorrow = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")
$nextWeek = (Get-Date).AddDays(7).ToString("yyyy-MM-dd")
$now = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
$nowPlus1 = (Get-Date).AddHours(1).ToString("yyyy-MM-ddTHH:mm:ss")

$pass = 0
$fail = 0
$results = @()

function Invoke-Test {
    param([string]$Label, [string]$Method, [string]$Uri, [string]$Body)
    $fullUri = "$baseUrl$Uri"
    Write-Host "`n[$Method] $Uri" -ForegroundColor Cyan
    Write-Host "  => $Label"
    try {
        $params = @{ Method = $Method; Uri = $fullUri; Headers = $headers; ErrorAction = "Stop" }
        if ($Body) { $params["Body"] = $Body }
        $response = Invoke-RestMethod @params
        Write-Host "  Status : OK" -ForegroundColor Green
        if ($response.data -and $response.data.id) {
            Write-Host "  ID     : $($response.data.id)"
        }
        $script:pass++
        $script:results += [PSCustomObject]@{ Label = $Label; Method = $Method; Uri = $Uri; Status = "PASS"; Error = "" }
        return $response.data
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        try {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $errBody = $reader.ReadToEnd()
        }
        catch { $errBody = $_.Exception.Message }
        Write-Host "  Status : FAILED ($statusCode)" -ForegroundColor Red
        Write-Host "  Error  : $errBody" -ForegroundColor Yellow
        $script:fail++
        $script:results += [PSCustomObject]@{ Label = $Label; Method = $Method; Uri = $Uri; Status = "FAIL ($statusCode)"; Error = $errBody }
        return $null
    }
}

Write-Host "============================================================" -ForegroundColor Magenta
Write-Host "   SmartLogix -- Full Integration Test (All Endpoints)     " -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta

# ============================================================
# 1. USERS
# ============================================================
Write-Host "`n=== MODULE: USERS ===" -ForegroundColor Yellow

$userBody = ConvertTo-Json @{
    name = "Bob Smith $ts"; role = "DRIVER"
    email = "bob.smith.$ts@smartlogix.io"
    phone = "91-98765-$ts"; password = "Passw0rd!$ts"
}
$user = Invoke-Test -Label "Create User (DRIVER)" -Method POST -Uri "/users" -Body $userBody

$adminBody = ConvertTo-Json @{
    name = "Admin User $ts"; role = "ADMINISTRATOR"
    email = "admin.$ts@smartlogix.io"
    phone = "91-11111-$ts"; password = "AdminPass!$ts"
}
$adminUser = Invoke-Test -Label "Create Admin User" -Method POST -Uri "/users" -Body $adminBody

if ($user) {
    $null = Invoke-Test -Label "Get User by ID"    -Method GET -Uri "/users/$($user.id)"
    $null = Invoke-Test -Label "Get User by Email" -Method GET -Uri "/users/by-email?email=bob.smith.$ts%40smartlogix.io"
    $null = Invoke-Test -Label "Update User Status INACTIVE" -Method PATCH -Uri "/users/$($user.id)/status?status=INACTIVE"
    $null = Invoke-Test -Label "Update User Status ACTIVE"   -Method PATCH -Uri "/users/$($user.id)/status?status=ACTIVE"
}
$null = Invoke-Test -Label "List Users (paginated)" -Method GET -Uri "/users?page=0&size=5"

# ============================================================
# 2. MERCHANTS
# ============================================================
Write-Host "`n=== MODULE: MERCHANTS ===" -ForegroundColor Yellow

$merchantBody = ConvertTo-Json @{
    name             = "TechGadgets $ts"
    contactInfoJson  = '{"email":"ops@techgadgets.io","phone":"1800-100-200"}'
    billingTermsJson = '{"net":30,"currency":"USD"}'
}
$merchant = Invoke-Test -Label "Create Merchant" -Method POST -Uri "/merchants" -Body $merchantBody

if ($merchant) {
    $null = Invoke-Test -Label "Get Merchant by ID" -Method GET -Uri "/merchants/$($merchant.id)"
}
$null = Invoke-Test -Label "List Merchants" -Method GET -Uri "/merchants?page=0&size=5"

# ============================================================
# 3. SERVICE ZONES
# ============================================================
Write-Host "`n=== MODULE: SERVICE ZONES ===" -ForegroundColor Yellow

$zoneBody = ConvertTo-Json @{
    name = "North Mumbai $ts"; timeZone = "Asia/Kolkata"; capacityPerSlot = 80
    polygonGeoJson = '{"type":"Polygon","coordinates":[[[72.8,18.9],[72.9,18.9],[72.9,19.0],[72.8,19.0],[72.8,18.9]]]}'
    postalCodesJson = '["400001","400002","400003"]'
    slaConfigJson = '{"standardHours":24,"expressHours":4}'
}
$zone = Invoke-Test -Label "Create Service Zone" -Method POST -Uri "/zones" -Body $zoneBody

if ($zone) {
    $null = Invoke-Test -Label "Get Zone by ID"   -Method GET  -Uri "/zones/$($zone.id)"
    $null = Invoke-Test -Label "Deactivate Zone"  -Method POST -Uri "/zones/$($zone.id)/deactivate"
    $null = Invoke-Test -Label "Activate Zone"    -Method POST -Uri "/zones/$($zone.id)/activate"
}
$null = Invoke-Test -Label "List Zones" -Method GET -Uri "/zones?page=0&size=5"

# ============================================================
# 4. DEPOTS
# ============================================================
Write-Host "`n=== MODULE: DEPOTS ===" -ForegroundColor Yellow

$depotBody = ConvertTo-Json @{
    name = "Mumbai Central Depot $ts"; timeZone = "Asia/Kolkata"
    addressJson = '{"street":"NH-8 Industrial Area","city":"Mumbai","state":"Maharashtra","pincode":"400063"}'
}
$depot = Invoke-Test -Label "Create Depot" -Method POST -Uri "/depots" -Body $depotBody

if ($depot) {
    $null = Invoke-Test -Label "Get Depot by ID" -Method GET -Uri "/depots/$($depot.id)"
}
$null = Invoke-Test -Label "List Depots" -Method GET -Uri "/depots?page=0&size=5"

# ============================================================
# 5. VEHICLES
# ============================================================
Write-Host "`n=== MODULE: VEHICLES ===" -ForegroundColor Yellow

$vehicleBody = ConvertTo-Json @{
    type = "TRUCK"; capacityKg = 3000.0; capacityVolumeM3 = 25.0
    registrationNumber = "MH-12-AB-$ts"
}
$vehicle = Invoke-Test -Label "Create Vehicle (TRUCK)" -Method POST -Uri "/vehicles" -Body $vehicleBody

if ($vehicle) {
    $null = Invoke-Test -Label "Get Vehicle by ID"              -Method GET   -Uri "/vehicles/$($vehicle.id)"
    $null = Invoke-Test -Label "Update Vehicle Status IN_USE"   -Method PATCH -Uri "/vehicles/$($vehicle.id)/status?status=IN_USE"
    $null = Invoke-Test -Label "Update Vehicle Status AVAILABLE"-Method PATCH -Uri "/vehicles/$($vehicle.id)/status?status=AVAILABLE"
}
$null = Invoke-Test -Label "List Vehicles"           -Method GET -Uri "/vehicles?page=0&size=5"
$null = Invoke-Test -Label "List Available Vehicles" -Method GET -Uri "/vehicles/available"

# ============================================================
# 6. DRIVERS
# ============================================================
Write-Host "`n=== MODULE: DRIVERS ===" -ForegroundColor Yellow
$driver = $null
if ($user) {
    $driverBody = ConvertTo-Json @{
        userId = $user.id; licenseNumber = "MH-DL-$ts"
        phone = "91-90000-$ts"; maxDailyHours = 9.0
    }
    $driver = Invoke-Test -Label "Create Driver" -Method POST -Uri "/drivers" -Body $driverBody

    if ($driver) {
        $null = Invoke-Test -Label "Get Driver by ID"           -Method GET   -Uri "/drivers/$($driver.id)"
        $null = Invoke-Test -Label "Update Driver Status ON_SHIFT"  -Method PATCH -Uri "/drivers/$($driver.id)/status?status=ON_SHIFT"
        $null = Invoke-Test -Label "Update Driver Status ACTIVE"-Method PATCH -Uri "/drivers/$($driver.id)/status?status=ACTIVE"
    }
    $null = Invoke-Test -Label "List All Drivers"      -Method GET -Uri "/drivers?page=0&size=5"
    $null = Invoke-Test -Label "List Available Drivers"-Method GET -Uri "/drivers/available"
}

# ============================================================
# 7. CARRIERS
# ============================================================
Write-Host "`n=== MODULE: CARRIERS ===" -ForegroundColor Yellow

$carrierBody = ConvertTo-Json @{
    name              = "FastShip Logistics $ts"
    contractTermsJson = '{"revShare":0.12,"minVolume":500}'
    allowedZonesJson  = '["North Mumbai","South Mumbai"]'
    maxWeightKg       = 5000
}
$carrier = Invoke-Test -Label "Create Carrier" -Method POST -Uri "/carriers" -Body $carrierBody

if ($carrier) {
    $null = Invoke-Test -Label "Get Carrier by ID"             -Method GET   -Uri "/carriers/$($carrier.id)"
    $null = Invoke-Test -Label "Update Carrier Status SUSPENDED"-Method PATCH -Uri "/carriers/$($carrier.id)/status?status=SUSPENDED"
    $null = Invoke-Test -Label "Update Carrier Status ACTIVE"  -Method PATCH -Uri "/carriers/$($carrier.id)/status?status=ACTIVE"
}
$null = Invoke-Test -Label "List Carriers" -Method GET -Uri "/carriers?page=0&size=5"

# ============================================================
# 8. PRICING RULES
# ============================================================
Write-Host "`n=== MODULE: PRICING RULES ===" -ForegroundColor Yellow

$pricingBody = ConvertTo-Json @{
    name = "Standard Rate $ts"
    conditionsJson = '{"serviceLevel":"STANDARD","minWeightKg":0,"maxWeightKg":50}'
    calculationJson = '{"baseRate":5.00,"perKgRate":1.50,"currency":"USD"}'
    effectiveFrom = $date; effectiveTo = $nextWeek; priority = 10
}
$pricingRule = Invoke-Test -Label "Create Pricing Rule" -Method POST -Uri "/pricing-rules" -Body $pricingBody

if ($pricingRule) {
    $null = Invoke-Test -Label "Get Pricing Rule by ID"          -Method GET  -Uri "/pricing-rules/$($pricingRule.id)"
    $null = Invoke-Test -Label "Activate Pricing Rule"           -Method POST -Uri "/pricing-rules/$($pricingRule.id)/activate"
    $null = Invoke-Test -Label "Get Active Pricing Rules for Date"-Method GET  -Uri "/pricing-rules/active?date=$date"
    $null = Invoke-Test -Label "Deactivate Pricing Rule"         -Method POST -Uri "/pricing-rules/$($pricingRule.id)/deactivate"
    $null = Invoke-Test -Label "Archive Pricing Rule"            -Method POST -Uri "/pricing-rules/$($pricingRule.id)/archive"
}
$null = Invoke-Test -Label "List All Pricing Rules" -Method GET -Uri "/pricing-rules?page=0&size=5"

# ============================================================
# 9. FULFILLMENTS
# ============================================================
Write-Host "`n=== MODULE: FULFILLMENTS ===" -ForegroundColor Yellow
$fulfillment = $null
if ($merchant -and $zone) {
    $fulfillmentBody = ConvertTo-Json @{
        orderId = "ORD-$ts-001"; merchantId = $merchant.id; serviceZoneId = $zone.id
        serviceLevel = "STANDARD"; packageWeightKg = 3.5; packageVolumeM3 = 0.08
        deliveryWindowStart = "${tomorrow}T09:00:00"; deliveryWindowEnd = "${tomorrow}T17:00:00"
    }
    $fulfillment = Invoke-Test -Label "Create Fulfillment" -Method POST -Uri "/fulfillments" -Body $fulfillmentBody

    if ($fulfillment) {
        $null = Invoke-Test -Label "Get Fulfillment by ID"      -Method GET -Uri "/fulfillments/$($fulfillment.id)"
        $null = Invoke-Test -Label "Get Fulfillment by Order ID"-Method GET -Uri "/fulfillments/by-order/ORD-$ts-001"
        $null = Invoke-Test -Label "List Fulfillments by Merchant"-Method GET -Uri "/fulfillments/merchant/$($merchant.id)"

        $sb1 = ConvertTo-Json @{ status = "ASSIGNED"; reason = "Payment verified" }
        $null = Invoke-Test -Label "Update Fulfillment ASSIGNED" -Method PATCH -Uri "/fulfillments/$($fulfillment.id)/status" -Body $sb1

        $sb2 = ConvertTo-Json @{ status = "EN_ROUTE"; reason = "Package picked up" }
        $null = Invoke-Test -Label "Update Fulfillment EN_ROUTE"-Method PATCH -Uri "/fulfillments/$($fulfillment.id)/status" -Body $sb2
    }
    $null = Invoke-Test -Label "List All Fulfillments"            -Method GET -Uri "/fulfillments?page=0&size=5"
    $null = Invoke-Test -Label "List Fulfillments EN_ROUTE"       -Method GET -Uri "/fulfillments?status=EN_ROUTE&page=0&size=5"
}

# ============================================================
# 10. TRACKING EVENTS
# ============================================================
Write-Host "`n=== MODULE: TRACKING EVENTS ===" -ForegroundColor Yellow
$trackingEvent = $null
if ($fulfillment) {
    $trackBody1 = ConvertTo-Json @{
        fulfillmentId = $fulfillment.id; eventType = "ASSIGNED"; timestamp = $now
        locationJson = '{"lat":18.9220,"lng":72.8347,"address":"Depot Gate, Mumbai"}'
        detailsJson = '{"scannedBy":"driver-001","condition":"GOOD"}'
    }
    $trackingEvent = Invoke-Test -Label "Record Tracking Event ASSIGNED" -Method POST -Uri "/tracking-events" -Body $trackBody1

    $trackBody2 = ConvertTo-Json @{
        fulfillmentId = $fulfillment.id; eventType = "OUT_FOR_DELIVERY"; timestamp = $nowPlus1
        locationJson = '{"lat":19.0760,"lng":72.8777,"address":"Andheri West, Mumbai"}'
        detailsJson = '{"estimatedArrival":"2 hours"}'
    }
    $null = Invoke-Test -Label "Record Tracking Event OUT_FOR_DELIVERY" -Method POST -Uri "/tracking-events" -Body $trackBody2

    if ($trackingEvent) {
        $null = Invoke-Test -Label "Get Tracking Event by ID" -Method GET -Uri "/tracking-events/$($trackingEvent.id)"
    }
    $null = Invoke-Test -Label "Get Full Tracking History" -Method GET -Uri "/tracking-events/fulfillment/$($fulfillment.id)"
}

# ============================================================
# 11. PROOF OF DELIVERY
# ============================================================
Write-Host "`n=== MODULE: PROOF OF DELIVERY ===" -ForegroundColor Yellow


# Fulfillment must be EN_ROUTE when POD is captured.
# The capture() service auto-marks fulfillment as DELIVERED.
$podBody = ConvertTo-Json @{
    fulfillmentId = $fulfillment.id; deliveredAt = $now; deliveredById = $user.id
    photoUrisJson = '["https://cdn.smartlogix.io/pods/photo1.jpg"]'
    signatureUri = "https://cdn.smartlogix.io/pods/sig_001.png"
    signatureSha256 = "abc123def456"; quantityDelivered = 1
    notes = "Left at front door, customer signed"
}
$pod = Invoke-Test -Label "Capture Proof of Delivery (EN_ROUTE auto->DELIVERED)" -Method POST -Uri "/pods" -Body $podBody

if ($pod) {
    $null = Invoke-Test -Label "Get POD by ID"              -Method GET   -Uri "/pods/$($pod.id)"
    $null = Invoke-Test -Label "Get POD by Fulfillment ID"  -Method GET   -Uri "/pods/fulfillment/$($fulfillment.id)"
    $null = Invoke-Test -Label "Update POD Status VERIFIED" -Method PATCH -Uri "/pods/$($pod.id)/status?status=VERIFIED"
    $null = Invoke-Test -Label "Update POD Status DISPUTED" -Method PATCH -Uri "/pods/$($pod.id)/status?status=DISPUTED"
}

# ─────────────────────────────────────────────────────────────────────────────
# 12. DELIVERY EXCEPTIONS
#     Uses a fresh fulfillment (stays PENDING) to raise exception
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n=== MODULE: DELIVERY EXCEPTIONS ===" -ForegroundColor Yellow
$exception = $null
if ($merchant -and $zone -and $user) {
    $f2Body = ConvertTo-Json @{
        orderId = "ORD-$ts-002"; merchantId = $merchant.id; serviceZoneId = $zone.id
        serviceLevel = "EXPRESS"; packageWeightKg = 1.2; packageVolumeM3 = 0.03
        deliveryWindowStart = "${tomorrow}T10:00:00"; deliveryWindowEnd = "${tomorrow}T14:00:00"
    }
    $fulfillment2 = Invoke-Test -Label "Create 2nd Fulfillment (for exception)" -Method POST -Uri "/fulfillments" -Body $f2Body

    if ($fulfillment2) {
        $exBody = ConvertTo-Json @{
            fulfillmentId = $fulfillment2.id; raisedById = $user.id
            reasonCode = "ADDRESS_NOT_FOUND"
            details = "GPS navigation led to wrong building. No signage visible."
            suggestedAction = "Call customer and retry next day"
        }
        $exception = Invoke-Test -Label "Raise Delivery Exception" -Method POST -Uri "/exceptions" -Body $exBody

        if ($exception) {
            $null = Invoke-Test -Label "Get Exception by ID"            -Method GET  -Uri "/exceptions/$($exception.id)"
            $null = Invoke-Test -Label "Get Exceptions by Fulfillment"  -Method GET  -Uri "/exceptions/fulfillment/$($fulfillment2.id)"
            $null = Invoke-Test -Label "Count Open Exceptions"          -Method GET  -Uri "/exceptions/count/open"
            $null = Invoke-Test -Label "Escalate Exception"             -Method POST -Uri "/exceptions/$($exception.id)/escalate"
            $null = Invoke-Test -Label "Resolve Exception"              -Method POST -Uri "/exceptions/$($exception.id)/resolve?resolution=Customer+rescheduled"
        }
        $null = Invoke-Test -Label "List Open Exceptions" -Method GET -Uri "/exceptions?page=0&size=5"
    }
}

# ─────────────────────────────────────────────────────────────────────────────
# 13. DELIVERY RETURNS
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n=== MODULE: DELIVERY RETURNS ===" -ForegroundColor Yellow
$return = $null
if ($fulfillment) {
    $retBody = ConvertTo-Json @{
        fulfillmentId = $fulfillment.id
        returnLabelUri = "https://cdn.smartlogix.io/labels/RTN-$ts.pdf"
        pickupWindowStart = "${tomorrow}T09:00:00"; pickupWindowEnd = "${tomorrow}T13:00:00"
    }
    $return = Invoke-Test -Label "Initiate Delivery Return" -Method POST -Uri "/returns" -Body $retBody

    if ($return) {
        $null = Invoke-Test -Label "Get Return by ID" -Method GET -Uri "/returns/$($return.id)"
        $inspBody = '{"condition":"DAMAGED","notes":"Outer box crushed"}'
        $null = Invoke-Test -Label "Inspect Return"                    -Method POST  -Uri "/returns/$($return.id)/inspect" -Body $inspBody
        $null = Invoke-Test -Label "Update Return Status -> RECEIVED"  -Method PATCH -Uri "/returns/$($return.id)/status?status=RECEIVED"
        $null = Invoke-Test -Label "Update Return Status -> INSPECTED" -Method PATCH -Uri "/returns/$($return.id)/status?status=INSPECTED"
        $null = Invoke-Test -Label "Update Return Status -> RESTOCKED" -Method PATCH -Uri "/returns/$($return.id)/status?status=RESTOCKED"
    }
    $null = Invoke-Test -Label "List All Manifests" -Method GET -Uri "/manifests?page=0&size=5"
}

# ============================================================
# 15. CARRIER BOOKINGS
# ============================================================
Write-Host "`n=== MODULE: CARRIER BOOKINGS ===" -ForegroundColor Yellow
$booking = $null
if ($carrier -and $fulfillment) {
    $bkBody = ConvertTo-Json @{
        carrierId = $carrier.id; fulfillmentId = $fulfillment.id
        externalRef = "EXT-$ts-CBK"; feeAmount = 12.50; currency = "USD"
    }
    $booking = Invoke-Test -Label "Create Carrier Booking" -Method POST -Uri "/carrier-bookings" -Body $bkBody

    if ($booking) {
        $null = Invoke-Test -Label "Get Booking by ID"             -Method GET   -Uri "/carrier-bookings/$($booking.id)"
        $null = Invoke-Test -Label "Get Booking by Fulfillment ID" -Method GET   -Uri "/carrier-bookings/fulfillment/$($fulfillment.id)"
        $null = Invoke-Test -Label "List Bookings by Carrier"      -Method GET   -Uri "/carrier-bookings/carrier/$($carrier.id)?page=0&size=5"
        $null = Invoke-Test -Label "Update Booking Status CONFIRMED"-Method PATCH -Uri "/carrier-bookings/$($booking.id)/status?status=CONFIRMED"
        $null = Invoke-Test -Label "Cancel Carrier Booking"         -Method POST  -Uri "/carrier-bookings/$($booking.id)/cancel"
    }
}

# ─────────────────────────────────────────────────────────────────────────────
# 16. CARRIER SETTLEMENTS
#     State machine: DRAFT -> SUBMITTED -> APPROVED -> PAID
#     The controller exposes: approve() (SUBMITTED->APPROVED), dispute(), markPaid() (APPROVED->PAID)
#     Missing: submit() endpoint -- so we test: dispute() from DRAFT, then approve if submitted
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n=== MODULE: CARRIER SETTLEMENTS ===" -ForegroundColor Yellow
$settlement = $null
if ($carrier) {
    $stBody = ConvertTo-Json @{
        carrierId=$carrier.id; periodStart=$date; periodEnd=$tomorrow
    }
    $settlement = Invoke-Test -Label "Generate Carrier Settlement (DRAFT)" -Method POST -Uri "/settlements/generate" -Body $stBody

    if ($settlement) {
        $null = Invoke-Test -Label "Get Settlement by ID"        -Method GET  -Uri "/settlements/$($settlement.id)"
        $null = Invoke-Test -Label "List Settlements by Carrier" -Method GET  -Uri "/settlements/carrier/$($carrier.id)?page=0&size=5"

        # Dispute (available from any state per service code)
        $dispBody = '{"reason":"Incorrect fee calculation","claimed":9.50}'
        $null = Invoke-Test -Label "Dispute Settlement" -Method POST -Uri "/settlements/$($settlement.id)/dispute" -Body $dispBody
    }
    # List by valid statuses
    $null = Invoke-Test -Label "List Settlements by Status DRAFT"     -Method GET -Uri "/settlements?status=DRAFT&page=0&size=5"
    $null = Invoke-Test -Label "List Settlements by Status DISPUTED"  -Method GET -Uri "/settlements?status=DISPUTED&page=0&size=5"
}

# ─────────────────────────────────────────────────────────────────────────────
# FINAL SUMMARY
# ─────────────────────────────────────────────────────────────────────────────
Write-Host "`n============================================================" -ForegroundColor Magenta
Write-Host "   TEST SUMMARY" -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta
Write-Host "  PASSED : $pass" -ForegroundColor Green
Write-Host "  FAILED : $fail" -ForegroundColor Red
Write-Host "  TOTAL  : $($pass + $fail)"
Write-Host ""
Write-Host "  Detailed Results:"
$results | ForEach-Object {
    $color = if ($_.Status -eq "PASS") { "Green" } else { "Red" }
    Write-Host ("  [{0,-15}] [{1,-5}] {2}" -f $_.Status, $_.Method, $_.Label) -ForegroundColor $color
}
Write-Host "============================================================" -ForegroundColor Magenta
