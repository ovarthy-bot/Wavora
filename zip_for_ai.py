import os
import zipfile
from pathlib import Path

# Hariç tutulacak klasör ve dosya adları (tam eşleşme veya başlangıç kontrolü için)
EXCLUDE_DIRS = {
    '.git', '.github', '.gradle', '.kotlin', 'build', 
    '.idea', '.vscode', 'hs_err_pid'
}

EXCLUDE_EXTENSIONS = {
    '.class', '.log', '.pyc', '.zip', '.tar', '.gz', 
    '.jks', '.keystore', '.png', '.jpg', '.jpeg', '.webp'
}

EXCLUDE_FILES = {
    'local.properties', '.DS_Store', 'Thumbs.db'
}

def should_exclude(path: Path, root_path: Path) -> bool:
    # Göreceli yolu al
    rel_path = path.relative_to(root_path)
    
    # Herhangi bir üst dizin veya kendisi hariç tutulanlar listesindeyse
    for part in rel_path.parts:
        if part in EXCLUDE_DIRS:
            return True
            
    if path.name in EXCLUDE_FILES:
        return True
        
    if path.suffix.lower() in EXCLUDE_EXTENSIONS:
        return True
        
    return False

def create_ai_zip(source_dir='.', output_name='kotlin_project_for_ai.zip'):
    source_path = Path(source_dir).resolve()
    output_path = source_path / output_name
    
    print(f"🚀 Proje taranıyor ve zip dosyası hazırlanıyor: {output_path.name}")
    
    file_count = 0
    with zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(source_path):
            root_p = Path(root)
            
            # Alt dizinleri yerinde filtrele (gereksiz klasörlerin içine hiç girmesin)
            dirs[:] = [d for d in dirs if not should_exclude(root_p / d, source_path)]
            
            for file in files:
                file_p = root_p / file
                if not should_exclude(file_p, source_path):
                    # Çıktı zip dosyasının kendisini de harici tut
                    if file_p.resolve() == output_path.resolve():
                        continue
                        
                    arcname = file_p.relative_to(source_path)
                    zipf.write(file_p, arcname)
                    file_count += 1
                    print(f"Eklendi: {arcname}")

    print(f"\n✅ İşlem tamamlandı! Toplam {file_count} dosya sıkıştırıldı.")
    print(f"📦 Oluşan dosya: {output_path}")

if __name__ == '__main__':
    create_ai_zip()