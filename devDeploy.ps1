$ErrorActionPreference = "Stop"

Write-Host "Starting Database..."
Set-Location docker
docker-compose -p data -f compose-data.yml up -d
Set-Location ..

Write-Host "Starting Application in DEV mode..."
# Load .env variables
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#=]+?)\s*=\s*(.*?)\s*$') {
            $name = $matches[1]
            $value = $matches[2]
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

./gradlew bootRun --args='--spring.profiles.active=dev,mock'
