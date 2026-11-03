$cp = "D:\projects\Campus-Lost-Found-Portal\target\classes"
Get-ChildItem -LiteralPath "C:\Users\LOQ\.m2\repository" -Recurse -File -Filter "*.jar" | ForEach-Object { $cp += ";" + $_.FullName }
# Remove conflicting slf4j-api 1.7.36
$cp = $cp -replace "C:\\Users\\LOQ\\.m2\\repository\\org\\slf4j\\slf4j-api\\1.7.36\\slf4j-api-1.7.36.jar", ""
$cp | Out-File -FilePath "D:\projects\Campus-Lost-Found-Portal\classpath.txt" -Encoding ASCII
Write-Host "Fixed classpath"