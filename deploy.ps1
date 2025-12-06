$ErrorActionPreference = "Stop"

Write-Host "Building application..."
./gradlew build -x test

Set-Location docker

Write-Host "Starting Database..."
docker-compose -p data -f compose-data.yml up -d

Write-Host "Starting Application..."
docker-compose -p agentic-prediction-cloud -f compose-prod.yml --env-file ../.env up -d --build

Write-Host "Deployment complete."
Set-Location ..
