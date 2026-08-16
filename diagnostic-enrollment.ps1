Write-Host "`n===== ENTITY: Enrollment.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "Enrollment.java" |
    Where-Object { $_.FullName -match "entity" } |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== REPOSITORY: EnrollmentRepository.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "EnrollmentRepository.java" |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== enums: EnrollmentStatus.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "EnrollmentStatus.java" |
    ForEach-Object { Get-Content $_.FullName }
