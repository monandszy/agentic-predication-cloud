$ErrorActionPreference = "Stop"

Write-Host "Loading environment variables..."
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#=]+?)\s*=\s*(.*?)\s*$') {
            $name = $matches[1]
            $value = $matches[2]
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

Write-Host "Running Atlantis Scenario Simulation..."
./gradlew test --tests pl.msz.apc.agents.AtlantisScenarioTest --rerun-tasks
