$exclude = @(
  "slf4j-api\1.7.36",
  "commons-io\2.11.0",
  "commons-io\2.13.0",
  "commons-io\2.15.1",
  "jackson-annotations\2.14.2",
  "jackson-core\2.14.2",
  "jackson-databind\2.14.2",
  "jackson-module-parameter-names\2.14.2",
  "micrometer-commons\1.12.11",
  "micrometer-observation\1.12.11",
  "junit-platform-commons\1.9.3",
  "junit-platform-engine\1.9.3",
  "junit-platform-launcher\1.9.3",
  "opentest4j\1.2.0",
  "commons-lang3\3.12.0",
  "commons-compress\1.25.0",
  "plexus-interpolation\1.26",
  "plexus-utils\3.5.1",
  "plexus-utils\4.0.0",
  "antlr4-runtime\4.7.2",
  "asm\9.5"
)
$cp = "D:\projects\Campus-Lost-Found-Portal\target\classes"
Get-ChildItem -LiteralPath "C:\Users\LOQ\.m2\repository" -Recurse -File -Filter "*.jar" | ForEach-Object {
  $path = $_.FullName
  $skip = $false
  foreach ($e in $exclude) {
    if ($path -like "*$e*") { $skip = $true; break }
  }
  if (-not $skip) { $cp += ";" + $path }
}
$cp | Out-File -FilePath "D:\projects\Campus-Lost-Found-Portal\classpath.txt" -Encoding ASCII -NoNewline
$env:CP = $cp
& java -cp $env:CP com.campus.lostfound.CampusLostFoundApplication