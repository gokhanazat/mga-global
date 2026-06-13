Add-Type -AssemblyName System.Drawing

function Resize-Image-With-Padding {
    param (
        [string]$InputPath,
        [string]$OutputPath,
        [int]$Width,
        [int]$Height,
        [float]$ScaleFactor
    )
    $src = [System.Drawing.Image]::FromFile($InputPath)
    $dest = New-Object System.Drawing.Bitmap($Width, $Height)
    $g = [System.Drawing.Graphics]::FromImage($dest)
    
    $g.Clear([System.Drawing.Color]::Transparent)
    
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    
    # Calculate padded size
    $targetWidth = [int]($Width * $ScaleFactor)
    $targetHeight = [int]($Height * $ScaleFactor)
    $offsetX = [int](($Width - $targetWidth) / 2)
    $offsetY = [int](($Height - $targetHeight) / 2)
    
    $g.DrawImage($src, $offsetX, $offsetY, $targetWidth, $targetHeight)
    
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
$commonDrawableDir = "d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\commonMain\composeResources\drawable"

# 1. Resize Adaptive Foreground (Safe Zone is 66% of the 108dp size to prevent clipping by system launcher)
Resize-Image-With-Padding $logoPath "$resDir\mipmap-mdpi\ic_launcher_foreground.png" 108 108 0.65
Resize-Image-With-Padding $logoPath "$resDir\mipmap-hdpi\ic_launcher_foreground.png" 162 162 0.65
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xhdpi\ic_launcher_foreground.png" 216 216 0.65
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxhdpi\ic_launcher_foreground.png" 324 324 0.65
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxxhdpi\ic_launcher_foreground.png" 432 432 0.65

# 2. Resize Legacy Launcher Icons (85% scale to have nice margins)
Resize-Image-With-Padding $logoPath "$resDir\mipmap-mdpi\ic_launcher.png" 48 48 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-hdpi\ic_launcher.png" 72 72 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xhdpi\ic_launcher.png" 96 96 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxhdpi\ic_launcher.png" 144 144 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxxhdpi\ic_launcher.png" 192 192 0.85

# 3. Resize Legacy Round Launcher Icons (85% scale)
Resize-Image-With-Padding $logoPath "$resDir\mipmap-mdpi\ic_launcher_round.png" 48 48 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-hdpi\ic_launcher_round.png" 72 72 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xhdpi\ic_launcher_round.png" 96 96 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxhdpi\ic_launcher_round.png" 144 144 0.85
Resize-Image-With-Padding $logoPath "$resDir\mipmap-xxxhdpi\ic_launcher_round.png" 192 192 0.85

# 4. In-App Logo in common resources (itso_global_logo.png) (80% scale to look neat in top bar)
Resize-Image-With-Padding $logoPath "$commonDrawableDir\itso_global_logo.png" 256 256 0.80

Write-Host "All icons successfully resized with custom scale and transparent padding!"
