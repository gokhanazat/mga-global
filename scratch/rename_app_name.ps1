# Find and replace "ITSO GLOBAL" with "MGA GLOBAL" in all files under the project
$searchPaths = @(
    "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp",
    "d:\AndroidStudioProjects\MGA_GLOBAL\trade-admin"
)

foreach ($path in $searchPaths) {
    if (Test-Path $path) {
        $files = Get-ChildItem -Path $path -Recurse | Where-Object { $_.FullName -notmatch "node_modules" -and $_.FullName -notmatch "build" -and $_.FullName -notmatch "\.git" -and $_.FullName -notmatch "\.gradle" -and !$_.PsIsContainer }
        foreach ($file in $files) {
            if ($file.Extension -in @(".kt", ".xml", ".gradle", ".kts", ".json", ".js", ".jsx", ".html")) {
                $content = Get-Content -Path $file.FullName -Raw
                $modified = $false
                
                # Check for "ITSO GLOBAL"
                if ($content -match "ITSO GLOBAL") {
                    $content = $content -replace "ITSO GLOBAL", "MGA GLOBAL"
                    $modified = $true
                }
                
                # Check for lowercase "itso global"
                if ($content -match "itso global") {
                    $content = $content -replace "itso global", "mga global"
                    $modified = $true
                }

                # Check for "ITSO" (only if referring to app, e.g. "ITSO Platformu" or "ITSO'dan")
                # But to be safe, let's only replace specific occurrences or generally in strings.xml
                if ($file.Name -eq "strings.xml") {
                    if ($content -match "ITSO") {
                        $content = $content -replace "ITSO", "MGA"
                        $modified = $true
                    }
                }

                if ($modified) {
                    Set-Content -Path $file.FullName -Value $content -Encoding utf8
                    Write-Output "Updated app name in: $($file.FullName)"
                }
            }
        }
    }
}
