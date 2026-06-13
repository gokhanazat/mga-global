# 1. Rename directories
$sourceSets = @("androidMain", "commonMain", "commonTest", "iosMain", "jsMain", "wasmJsMain")
foreach ($set in $sourceSets) {
    $dir = "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\$set\kotlin\com\mgacreative\globaltrade"
    $newDir = "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\$set\kotlin\com\mgacreative\mgaglobal"
    if (Test-Path $dir) {
        # Create target parent dir just in case
        $parentDir = Split-Path -Path $newDir -Parent
        if (!(Test-Path $parentDir)) {
            New-Item -ItemType Directory -Force -Path $parentDir
        }
        Rename-Item -Path $dir -NewName "mgaglobal" -Force
        Write-Output "Renamed $dir to mgaglobal"
    } else {
        Write-Output "Directory not found: $dir"
    }
}

# 2. Find and replace in all files under composeApp/src and composeApp/build.gradle.kts
$files = Get-ChildItem -Path "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp" -Recurse | Where-Object { $_.FullName -notmatch "composeApp\\build" -and !$_.PsIsContainer }
foreach ($file in $files) {
    if ($file.Extension -in @(".kt", ".xml", ".gradle", ".kts", ".json")) {
        $content = Get-Content -Path $file.FullName -Raw
        if ($content -match "com.mgacreative.globaltrade") {
            $newContent = $content -replace "com.mgacreative.globaltrade", "com.mgacreative.mgaglobal"
            Set-Content -Path $file.FullName -Value $newContent -Encoding utf8
            Write-Output "Updated: $($file.FullName)"
        }
    }
}
