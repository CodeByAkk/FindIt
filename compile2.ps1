$cp = "D:\projects\Campus-Lost-Found-Portal\target\classes"
Get-ChildItem -LiteralPath "C:\Users\LOQ\.m2\repository" -Recurse -File -Filter "*.jar" | ForEach-Object { $cp += ";" + $_.FullName }
$cp = $cp -replace ';', "`n"
$cp | Out-File -FilePath "D:\projects\Campus-Lost-Found-Portal\cp_arg.txt" -Encoding ASCII
$srcDir = "D:\projects\Campus-Lost-Found-Portal\src\main\java"
$classesDir = "D:\projects\Campus-Lost-Found-Portal\target\classes"
javac -cp "@D:\projects\Campus-Lost-Found-Portal\cp_arg.txt" -d "$classesDir" "$srcDir\com\campus\lostfound\config\SecurityConfig.java" "$srcDir\com\campus\lostfound\config\PortalAuthenticationProvider.java" 2>&1