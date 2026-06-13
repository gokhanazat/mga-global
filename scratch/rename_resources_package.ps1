# Find and replace "itsohub.composeapp" with "mgaglobal.composeapp" in all files under the project
$searchPaths = @(
    "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp"
)

foreach ($path in $searchPaths) {
    if (Test-Path $path) {
        $files = Get-ChildItem -Path $path -Recurse | Where-Object { $_.FullName -notmatch "node_modules" -and $_.FullName -notmatch "build" -and $_.FullName -notmatch "\.git" -and $_.FullName -notmatch "\.gradle" -and !$_.PsIsContainer }
        foreach ($file in $files) {
            if ($file.Extension -in @(".kt", ".xml", ".gradle", ".kts", ".json", ".js", ".jsx", ".html")) {
                $content = Get-Content -Path $file.FullName -Raw
                $modified = $false
                
                # Check for "itsohub.composeapp"
                if ($content -match "itsohub\.composeapp") {
                    $content = $content -replace "itsohub\.composeapp", "mgaglobal.composeapp"
                    $modified = $true
                }
                
                # Check for "itsohub/composeapp" (in case of resource pathing)
                if ($content -match "itsohub/composeapp") {
                    $content = $content -replace "itsohub/composeapp", "mgaglobal/composeapp"
                    $modified = $true
                }

                if ($modified) {
                    Set-Content -Path $file.FullName -Value $content -Encoding utf8
                    Write-Output "Updated resource package in: $($file.FullName)"
                }
            }
        }
    }
}
