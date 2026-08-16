$env:JAVA_HOME = "$env:USERPROFILE\.jdks\ms-21.0.12-1"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "JDK actif : " -NoNewline
& "$env:JAVA_HOME\bin\java.exe" -version

if ($args.Count -eq 0) {
    Write-Host "`nUsage: .\run.ps1 <commande maven>" -ForegroundColor Yellow
    Write-Host "Exemples:"
    Write-Host "  .\run.ps1 clean compile"
    Write-Host "  .\run.ps1 spring-boot:run"
    exit
}

.\mvnw.cmd @args