# Mide tiempos de respuesta de la API (30 muestras por endpoint)
# Antes: levantar la BD (docker compose up -d) y la app en el puerto 8081
# Uso: .\scripts\medir-metricas.ps1  -> genera metricas-tiempos.csv
param(
    [string]$BaseUrl = "http://localhost:8081",
    [int]$Muestras = 30
)

$ErrorActionPreference = "Stop"
$userId = "11111111-1111-1111-1111-111111111111"   # usuario demo
$json = "application/json"

function Invoke-Api($Method, $Path, $Body) {
    if ($Body) {
        return Invoke-RestMethod -Method $Method -Uri "$BaseUrl$Path" -ContentType $json -Body ($Body | ConvertTo-Json)
    }
    return Invoke-RestMethod -Method $Method -Uri "$BaseUrl$Path"
}

# datos para medir: un programa con un dia y un plan
$programa = Invoke-Api POST "/api/workout-programs" @{ userId = $userId; name = "Medicion $(Get-Date -Format s)"; splitType = "PUSH_PULL_LEGS" }
Invoke-Api POST "/api/workout-programs/$($programa.id)/days" @{ dayOfWeek = "MONDAY" } | Out-Null
$plan = Invoke-Api POST "/api/supplement-plans" @{ userId = $userId; linkedProgramId = $programa.id }

# nombres distintos para no chocar con la regla de duplicados
$endpoints = @(
    @{ Nombre = "GET /api/workout-programs?userId=";        Accion = { param($i) Invoke-Api GET "/api/workout-programs?userId=$userId" } },
    @{ Nombre = "GET /api/workout-programs/{id}";           Accion = { param($i) Invoke-Api GET "/api/workout-programs/$($programa.id)" } },
    @{ Nombre = "GET /api/supplement-plans?userId=";        Accion = { param($i) Invoke-Api GET "/api/supplement-plans?userId=$userId" } },
    @{ Nombre = "POST /api/workout-programs/{id}/exercises"; Accion = { param($i) Invoke-Api POST "/api/workout-programs/$($programa.id)/exercises" @{ dayOfWeek = "MONDAY"; exerciseName = "Ejercicio $i"; muscleGroup = "Pecho"; sets = 4; repsPerSet = 8; restSeconds = 90 } } },
    @{ Nombre = "POST /api/supplement-plans/{id}/doses";    Accion = { param($i) Invoke-Api POST "/api/supplement-plans/$($plan.id)/doses" @{ supplementName = "Suplemento $i"; grams = 5; timeOfDay = "08:00"; relativeToWorkout = $false } } },
    @{ Nombre = "POST /api/workout-programs";               Accion = { param($i) Invoke-Api POST "/api/workout-programs" @{ userId = $userId; name = "Medicion $i"; splitType = "FULL_BODY" } } }
)

$resultados = foreach ($ep in $endpoints) {
    & $ep.Accion "calentamiento" | Out-Null          # calentamiento, no cuenta
    $tiempos = for ($i = 1; $i -le $Muestras; $i++) {
        $reloj = [System.Diagnostics.Stopwatch]::StartNew()
        & $ep.Accion $i | Out-Null
        $reloj.Stop()
        $reloj.Elapsed.TotalMilliseconds
    }
    $ordenados = $tiempos | Sort-Object
    $p95 = $ordenados[[math]::Ceiling(0.95 * $ordenados.Count) - 1]
    [pscustomobject]@{
        Endpoint   = $ep.Nombre
        PromedioMs = [math]::Round(($tiempos | Measure-Object -Average).Average, 1)
        P95Ms      = [math]::Round($p95, 1)
        MaximoMs   = [math]::Round(($tiempos | Measure-Object -Maximum).Maximum, 1)
    }
}

$resultados | Format-Table -AutoSize
$resultados | Export-Csv -Path "metricas-tiempos.csv" -NoTypeInformation -Encoding UTF8
Write-Host "Resultados guardados en metricas-tiempos.csv"
