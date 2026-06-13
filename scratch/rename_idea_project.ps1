# Update .idea/.name
$nameFile = "d:\AndroidStudioProjects\MGA_GLOBAL\.idea\.name"
if (Test-Path $nameFile) {
    Set-Content -Path $nameFile -Value "MGAGLOBAL" -Encoding utf8
    Write-Output "Updated: $nameFile"
}

# Update all XML files under .idea
$ideaFiles = Get-ChildItem -Path "d:\AndroidStudioProjects\MGA_GLOBAL\.idea" -Recurse | Where-Object { !$_.PsIsContainer }
foreach ($file in $ideaFiles) {
    if ($file.Extension -in @(".xml", ".name", ".iml")) {
        $content = Get-Content -Path $file.FullName -Raw
        if ($content -match "ITSOHUB") {
            $newContent = $content -replace "ITSOHUB", "MGAGLOBAL"
            Set-Content -Path $file.FullName -Value $newContent -Encoding utf8
            Write-Output "Updated: $($file.FullName)"
        }
    }
}
