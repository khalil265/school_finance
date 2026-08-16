Write-Host "`n===== ENTITY: Student.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "Student.java" |
    Where-Object { $_.FullName -match "entity" } |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== ENTITY: AcademicYear.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "AcademicYear.java" |
    Where-Object { $_.FullName -match "entity" } |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== DTO: StudentResponse / StudentRequest (si existants) =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File |
    Where-Object { $_.Name -match "^Student(Response|Request|DTO)\.java$" } |
    ForEach-Object {
        Write-Host "`n--- $($_.FullName) ---" -ForegroundColor Yellow
        Get-Content $_.FullName
    }

Write-Host "`n===== MAPPER: StudentMapper.java (si existant) =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "StudentMapper.java" |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== CONTROLLER: StudentController.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "StudentController.java" |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== SERVICE: StudentService.java =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "StudentService.java" |
    ForEach-Object { Get-Content $_.FullName }

Write-Host "`n===== Recherche de toute reference a AcademicYear dans le module Student =====" -ForegroundColor Cyan
Get-ChildItem .\src\main\java -Recurse -File -Filter "Student*.java" |
    Select-String -Pattern "AcademicYear" |
    Select-Object Path, LineNumber, Line
