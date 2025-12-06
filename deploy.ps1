$ErrorActionPreference = "Stop"

Write-Host "Building application..."
./gradlew build -x test

Set-Location docker

Write-Host "Starting Database..."
docker-compose -p data -f compose-data.yml up -d

Write-Host "Starting Application..."

# Load .env variables
if (Test-Path "../.env") {
    Get-Content "../.env" | ForEach-Object {
        if ($_ -match '^\s*([^#=]+?)\s*=\s*(.*?)\s*$') {
            $name = $matches[1]
            $value = $matches[2]
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

docker-compose -p agentic-prediction-cloud -f compose-prod.yml up -d --build

Write-Host "Deployment complete."
Set-Location ..
