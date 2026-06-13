import os

def fix_file(filepath):
    try:
        with open(filepath, 'rb') as f:
            data = f.read()
        
        # Decode using 'utf-8-sig' to automatically strip BOM (byte order mark) if present
        text = data.decode('utf-8-sig', errors='ignore')
        
        # Check if there are corrupted sequences
        corrupted_markers = ["Ã¼", "Ã¶", "Ä±", "ÅŸ", "Ã§", "Ã–", "Ä°", "Ã¢", "ÅŸ", "ÄŸ", "Ãœ", "Ã¦", "ÅŸ", "ÄŸ", "Ä°", "Ã‡", "ÃŸ", "Ã¢", "â€", "âœ“"]
        if any(marker in text for marker in corrupted_markers):
            # Encode using cp1252 (Windows-1252) to get the raw bytes, then decode as UTF-8
            corrected = text.encode('cp1252', errors='ignore').decode('utf-8', errors='ignore')
            
            with open(filepath, 'w', encoding='utf-8', newline='') as f:
                f.write(corrected)
            print(f"Fixed: {filepath}")
    except Exception as e:
        print(f"Failed to fix {filepath}: {e}")

walk_dir = r"d:\AndroidStudioProjects\MGA_GLOBAL\composeApp\src\commonMain\kotlin"
for root, dirs, files in os.walk(walk_dir):
    for file in files:
        if file.endswith('.kt'):
            fix_file(os.path.join(root, file))
