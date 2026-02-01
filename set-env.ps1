# Environment Variables Setup
# For PowerShell (Windows)
$env:REPOSITORY_ROOT = "D:\HKII_4\Seminar\HP-Seminar-Vibe-Coding-SGU"

# For Bash/Zsh (Linux/Mac)
# export REPOSITORY_ROOT="D:\HKII_4\Seminar\HP-Seminar-Vibe-Coding-SGU"

Write-Host "REPOSITORY_ROOT has been set to: $env:REPOSITORY_ROOT"

# Verify the path exists
if (Test-Path $env:REPOSITORY_ROOT) {
    Write-Host "✅ Repository root path verified: $env:REPOSITORY_ROOT" -ForegroundColor Green
} else {
    Write-Host "❌ Repository root path not found: $env:REPOSITORY_ROOT" -ForegroundColor Red
}
