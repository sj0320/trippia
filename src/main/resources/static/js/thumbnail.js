window.previewThumbnail = function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('thumbnail-preview');
            const previewJs = document.getElementById('thumbnail-preview-js');

            if (preview) preview.src = e.target.result;
            if (previewJs) {
                previewJs.src = e.target.result;
                previewJs.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}