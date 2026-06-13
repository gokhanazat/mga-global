Add-Type -AssemblyName System.Drawing

function Resize-Image {
    param (
        [string]$InputPath,
        [string]$OutputPath,
        [int]$Width,
        [int]$Height
    )
    $src = [System.Drawing.Image]::FromFile($InputPath)
    $dest = New-Object System.Drawing.Bitmap($Width, $Height)
    $g = [System.Drawing.Graphics]::FromImage($dest)
    
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    
    $g.DrawImage($src, 0, 0, $Width, $Height)
    
    # Ensure directory exists
    $dir = Split-Path -Parent $OutputPath
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Force -Path $dir
    }
    
    # Delete old file if exists
    if (Test-Path $OutputPath) {
        Remove-Item -Force $OutputPath
    }
    
    $dest.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    
    $g.Dispose()
    $dest.Dispose()
    $src.Dispose()
}

$logoPath = "d:\AndroidStudioProjects\MGA_GLOBAL\yeni_logo.png"
$resDir = "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\androidMain\res"

Resize-Image $logoPath "$resDir\mipmap-mdpi\ic_launcher_foreground.png" 108 108
Resize-Image $logoPath "$resDir\mipmap-hdpi\ic_launcher_foreground.png" 162 162
Resize-Image $logoPath "$resDir\mipmap-xhdpi\ic_launcher_foreground.png" 216 216
Resize-Image $logoPath "$resDir\mipmap-xxhdpi\ic_launcher_foreground.png" 324 324
Resize-Image $logoPath "$resDir\mipmap-xxxhdpi\ic_launcher_foreground.png" 432 432

Write-Host "Logo successfully resized and copied to all mipmap directories!"
