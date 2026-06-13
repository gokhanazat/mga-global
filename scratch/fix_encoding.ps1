try {
    [System.Text.Encoding]::RegisterProvider([System.Text.CodePagesEncodingProvider]::Instance)
} catch {}

$path = "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\commonMain\composeResources\values\strings.xml"
$content = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)

# Convert from CP1252 to UTF-8
$bytes = [System.Text.Encoding]::GetEncoding("Windows-1252").GetBytes($content)
$corrected = [System.Text.Encoding]::UTF8.GetString($bytes)

# Let's check if there are still some typical patterns or if it looks correct.
# Replace any specific remaining broken symbols if necessary.
# e.g., double encoding or specific characters like "âœ“" -> "✓" (UTF-8 bytes of ✓: E2 9C 93, in CP1252: â œ “)
# Let's write the corrected content back
[System.IO.File]::WriteAllText($path, $corrected, [System.Text.Encoding]::UTF8)

Write-Host "strings.xml encoding corrected!"
