tinymce.init({
    language: "ko_KR",
    selector: '#editor',
    height: 600,
    plugins: [
        'image', 'media', 'code', 'lists', 'link', 'table', 'autolink',
        'advlist', 'preview', 'fullscreen', 'help', 'wordcount'
    ],
    toolbar: `
        undo redo | blocks fontselect fontsizeselect |
        bold italic underline forecolor backcolor |
        alignleft aligncenter alignright alignjustify |
        bullist numlist outdent indent |
        link image media | table | code fullscreen help
    `,
    menubar: 'file edit view insert format tools table help',
    automatic_uploads: true,
    file_picker_types: 'image',

    // 👇 여기만 수정!
    images_upload_handler: function (blobInfo) {
        return new Promise((resolve, reject) => {
            const file = blobInfo.blob();
            const formData = new FormData();
            formData.append('upload', file);

            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

            const xhr = new XMLHttpRequest();
            xhr.open('POST', '/api/file/upload', true);
            xhr.setRequestHeader(csrfHeader, csrfToken);

            xhr.onload = function () {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    resolve(response.url);  // ✅ 여기서 이미지 주소만 넘김
                } else {
                    reject(`Upload failed: ${xhr.status}`);
                }
            };

            xhr.onerror = function () {
                reject('Upload failed due to a network error.');
            };

            xhr.send(formData);
        });
    },

    content_style: `
        img {
            max-width: 100%;
            height: auto;
        }
    `
});